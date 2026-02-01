package com.ktran.flashsale_core.services.rabbitmq;

import com.ktran.flashsale_core.configuration.RabbitConfig;
import com.ktran.flashsale_core.dtos.OrderDto;
import com.ktran.flashsale_core.entities.*;
import com.ktran.flashsale_core.exceptions.AppException;
import com.ktran.flashsale_core.exceptions.ErrorCode;
import com.ktran.flashsale_core.repositorys.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderConsumer {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderHistoryRepository historyRepository;
    private final UserRepository userRepository;

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    @Transactional
    public void consumeOrder(OrderDto orderDto) {
        log.info("Dang xu ly don hang cho User: {}", orderDto);
        try {
            User user = userRepository.findById(orderDto.getUserId())
                    .orElseThrow(() -> new AppException(ErrorCode.USERNAME_INVALID));
            //Xem MQ active ?
//            System.out.println(" Đang xử lý đơn của User: " + orderDto.getUserId() + " (Đợi tí...)");
//            Thread.sleep(2000);
            Order order = Order.builder()
                    .user(user)
                    .status("PENDING")
                    .paymentMethod(orderDto.getPaymentMethod())
                    .createdAt(LocalDateTime.now())
                    .totalPrice(BigDecimal.ZERO)
                    .build();

            List<OrderItem> orderItems = new ArrayList<>();
            BigDecimal finalTotalPrice = BigDecimal.ZERO;
            for(var itemReq : orderDto.getItems()) {
                //Xu ly viec tranh chap(su dung atomic update ) neu dung multi threads
                int rowsAffected = productRepository.deductStockAtomic(
                        itemReq.getProductId(), itemReq.getQuantity()
                );

                if(rowsAffected == 0) {
                    throw new AppException(ErrorCode.PRODUCT_OUT_OF_STOCK);
                }

                Product product = productRepository.findById(itemReq.getProductId())
                        .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));


                OrderItem orderItem = OrderItem.builder()
                        .order(order)
                        .product(product)
                        .quantity(itemReq.getQuantity())
                        .price(product.getSalePrice())
                        .build();
                orderItems.add(orderItem);

                BigDecimal itemTotal = product.getSalePrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
                finalTotalPrice = finalTotalPrice.add(itemTotal);
            }
            order.setOrderItems(orderItems);
            order.setTotalPrice(finalTotalPrice);

            Order savedOrder = orderRepository.save(order);
            OrderHistory history = OrderHistory.builder()
                    .order(savedOrder)
                    .status("PENDING")
                    .note("Đơn hàng tạo thành công")
                    .changedAt(LocalDateTime.now())
                    .build();
            historyRepository.save(history);

            log.info("Đã lưu xong Order ID: {}", savedOrder.getId());
        } catch (Exception e) {
            log.error("Lỗi khi lưu db: {}", e.getMessage());
        }
    }
}
