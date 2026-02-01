package com.ktran.flashsale_core.responses;

import com.ktran.flashsale_core.entities.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class PaymentResponse {
    private Long id;
    private String paymentMethod;
    private String transactionCode;
    private BigDecimal amount;
    private String status;
    private LocalDateTime paymentTime;

    private String paymentUrl;

    public static PaymentResponse fromPayment(Payment payment) {
        if (payment == null) return null;

        return PaymentResponse.builder()
                .id(payment.getId())
                .paymentMethod(payment.getPaymentMethod())
                .transactionCode(payment.getTransactionCode())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .paymentTime(payment.getPaymentTime())
                .build();
    }
}