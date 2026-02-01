package com.ktran.flashsale_core.services;

import com.ktran.flashsale_core.dtos.OrderDto;
import com.ktran.flashsale_core.dtos.OrderItemDto;
import com.ktran.flashsale_core.entities.*;
import com.ktran.flashsale_core.exceptions.AppException;
import com.ktran.flashsale_core.exceptions.ErrorCode;
import com.ktran.flashsale_core.repositorys.OrderRepository;
import com.ktran.flashsale_core.repositorys.OrderHistoryRepository;
import com.ktran.flashsale_core.responses.OrderResponse;
import com.ktran.flashsale_core.services.rabbitmq.OrderProducer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderHistoryRepository historyRepository;

    private final RedisTemplate<String, Object> redisTemplate;
    private final DefaultRedisScript<Long> stockScript;

    private final OrderProducer orderProducer;

    public OrderResponse createOrder(OrderDto orderDto) {
        List<OrderItemDto> deductedItems = new ArrayList<>();

        for(var itemReq : orderDto.getItems()) {
            String redisKey = "product:stock:" + itemReq.getProductId();
            Long result = redisTemplate.execute(
                    stockScript,
                    List.of(redisKey),
                    itemReq.getQuantity()
            );

            if (result == null || result == 0) {
                rollbackRedisStock(deductedItems);
                throw new AppException(ErrorCode.PRODUCT_OUT_OF_STOCK);
            }
            deductedItems.add(itemReq);
        }

        try {
            orderProducer.sendOrderToQueue(orderDto);
        } catch (Exception e) {
            log.error("RabbitMQ lỗi, đang hoàn kho cho user: {}", orderDto.getUserId());
            rollbackRedisStock(deductedItems);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        return OrderResponse.builder()
                .id(null)
                .status("PROCESSING")
                .totalPrice(BigDecimal.ZERO)
                .build();
    }

    private void rollbackRedisStock(List<OrderItemDto> items) {
        for(var item : items) {
            try {
                String redisKey = "product:stock:" + item.getProductId();
                redisTemplate.opsForValue().increment(redisKey, item.getQuantity());
            } catch (Exception ex) {
                log.error("Lỗi hoàn kho Redis sp {}: {}", item.getProductId(), ex.getMessage());
            }
        }
    }

    @Transactional
    public OrderResponse updateStatus(Long orderId, String newStatus, String note) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        order.setStatus(newStatus);
        orderRepository.save(order);

        OrderHistory history = OrderHistory.builder()
                .order(order)
                .status(newStatus)
                .note(note)
                .changedAt(LocalDateTime.now())
                .build();
        historyRepository.save(history);

        return OrderResponse.fromOrder(order);
    }

    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));
        return OrderResponse.fromOrder(order);
    }

    public List<OrderResponse> getOrderByUserId(String userId) {
        List<Order> orders = orderRepository.findByUserId(String.valueOf(userId));
        return orders.stream()
                .map(OrderResponse::fromOrder)
                .collect(Collectors.toList());
    }
}