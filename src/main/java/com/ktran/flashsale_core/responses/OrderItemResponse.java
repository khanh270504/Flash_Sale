package com.ktran.flashsale_core.responses;

import com.ktran.flashsale_core.entities.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class OrderItemResponse {
    private Long id;
    private int quantity;
    private BigDecimal price;
    private Long productId;
    private String productName;
    private String productImage;

    public static OrderItemResponse fromOrderItem(OrderItem item) {
        if(item == null) return null;

        return OrderItemResponse.builder()
                .id(item.getId())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .productImage(item.getProduct().getImage())
                .build();
    }
}
