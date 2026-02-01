package com.ktran.flashsale_core.services;

import com.ktran.flashsale_core.dtos.PaymentCallBackDto;
import com.ktran.flashsale_core.dtos.PaymentDto;
import com.ktran.flashsale_core.entities.*;
import com.ktran.flashsale_core.exceptions.AppException;
import com.ktran.flashsale_core.exceptions.ErrorCode;
import com.ktran.flashsale_core.repositorys.OrderRepository;
import com.ktran.flashsale_core.repositorys.OrderHistoryRepository;
import com.ktran.flashsale_core.repositorys.PaymentRepository;
import com.ktran.flashsale_core.repositorys.ProductRepository;
import com.ktran.flashsale_core.responses.PaymentResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderHistoryRepository historyRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private final VNPayService vnPayService;

    @Transactional
    public PaymentResponse createPayment(PaymentDto dto) {
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        if ("PAID".equals(order.getStatus()) || "COMPLETED".equals(order.getStatus())) {
            throw new AppException(ErrorCode.ORDER_ALREADY_PAID);
        }
        String paymentMethod = order.getPaymentMethod() != null ? order.getPaymentMethod() : "VNPAY";

        Payment payment = paymentRepository.findByOrderId(order.getId()).orElse(null);
        if (payment == null) {
            payment = Payment.builder()
                    .order(order)
                    .amount(order.getTotalPrice())
                    .paymentMethod(paymentMethod)
                    .status("PENDING")
                    .paymentTime(LocalDateTime.now())
                    .build();
        } else {
            if (!"COMPLETED".equals(payment.getStatus())) {
                payment.setStatus("PENDING");
                payment.setPaymentMethod(paymentMethod);
                payment.setPaymentTime(LocalDateTime.now());
                payment.setTransactionCode(null);
            }
        }
        Payment savedPayment = paymentRepository.save(payment);
        if ("COD".equalsIgnoreCase(paymentMethod)) {
            payment.setTransactionCode("COD_" + order.getId());
            paymentRepository.save(payment);

            order.setStatus("CONFIRMED");
            orderRepository.save(order);

            saveHistory(order, "CONFIRMED", "Khách chọn thanh toán khi nhận hàng (COD)");
        }
        PaymentResponse response = PaymentResponse.fromPayment(savedPayment);
        if("VNPAY".equalsIgnoreCase(paymentMethod)) {
            String url = vnPayService.createVnPayUrl(
                    savedPayment.getAmount().longValue(),
                    "Thanh toán đơn hàng #" + order.getId(),
                    String.valueOf(savedPayment.getId())
            );
            response.setPaymentUrl(url);
        }

        return response;
    }

    @Transactional
    public PaymentResponse processPayment(PaymentCallBackDto dto) {
        Payment payment = paymentRepository.findById(dto.getPaymentId())
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_EXISTED));

        if (!"PENDING".equals(payment.getStatus())) {
            return PaymentResponse.fromPayment(payment);
        }

        Order order = payment.getOrder();

        String currentMethod = payment.getPaymentMethod();

        if (dto.isSuccess()) {
            payment.setStatus("COMPLETED");
            order.setStatus("PAID");


            payment.setTransactionCode(currentMethod + "_" + System.currentTimeMillis());

            orderRepository.save(order);
            paymentRepository.save(payment);

            saveHistory(order, "PAID", "Thanh toán thành công qua " + currentMethod);

        } else {
            payment.setStatus("FAILED");
            order.setStatus("CANCELLED");


            for (OrderItem item : order.getOrderItems()) {
                Product product = item.getProduct();

                productRepository.incrementStockAtomic(product.getId(), item.getQuantity());

                try {
                    String redisKey = "product:stock:" + product.getId();
                    redisTemplate.opsForValue().increment(redisKey, item.getQuantity());
                } catch (Exception e) {
                    log.error("Lỗi hoàn kho Redis sp {}: {}", product.getId(), e.getMessage());
                }
            }


            orderRepository.save(order);
            paymentRepository.save(payment);

            saveHistory(order, "CANCELLED", "Thanh toán thất bại (" + currentMethod + ") - Đã hoàn kho");
        }

        return PaymentResponse.fromPayment(payment);
    }

    private void saveHistory(Order order, String status, String note) {
        OrderHistory history = OrderHistory.builder()
                .order(order)
                .status(status)
                .note(note)
                .changedAt(LocalDateTime.now())
                .build();
        historyRepository.save(history);
    }
    public List<PaymentResponse> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

        return payments.stream()
                .map(PaymentResponse::fromPayment)
                .toList();
    }
}