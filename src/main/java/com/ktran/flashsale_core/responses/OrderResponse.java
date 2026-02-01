package com.ktran.flashsale_core.responses;

import com.ktran.flashsale_core.entities.Order;
import com.ktran.flashsale_core.entities.OrderHistory;
import com.ktran.flashsale_core.entities.OrderItem;
import com.ktran.flashsale_core.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class OrderResponse {
    private Long id;
    private String userName;
    private BigDecimal totalPrice;
    private String paymentMethod;
    private String status;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> orderItems;
    private List<OrderHistoryResponse> histories;
    private Long paymentId;

    public static OrderResponse fromOrder(Order order) {
        if (order == null) return null;
        return OrderResponse.builder()
                .id(order.getId())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .createdAt(order.getCreatedAt())
                .histories(order.getHistories() == null ? new ArrayList<>() : order.getHistories().stream()
                        .map(OrderHistoryResponse::fromOrderHistory) // Giả sử bạn có hàm này
                        .collect(Collectors.toList()))


                .orderItems(order.getOrderItems() == null ? new ArrayList<>() : order.getOrderItems().stream()
                        .map(OrderItemResponse::fromOrderItem)
                        .collect(Collectors.toList()))
                .build();
    }
}
