package com.ktran.flashsale_core.responses;

import com.ktran.flashsale_core.entities.OrderHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class OrderHistoryResponse {
    private Long id;

    private String status;
    private String note;
    @Builder.Default
    private LocalDateTime changedAt = LocalDateTime.now();

    public static OrderHistoryResponse fromOrderHistory(OrderHistory history) {
        if (history == null) return null;
        return OrderHistoryResponse.builder()
                .id(history.getId())
                .status(history.getStatus())
                .note(history.getNote())
                .build();
    }
}
