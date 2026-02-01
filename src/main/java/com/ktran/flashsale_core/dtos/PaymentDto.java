package com.ktran.flashsale_core.dtos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDto {
    private Long orderId;
    private String paymentMethod;
}
