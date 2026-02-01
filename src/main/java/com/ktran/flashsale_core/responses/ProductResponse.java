package com.ktran.flashsale_core.responses;

import com.ktran.flashsale_core.entities.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String image;
    private BigDecimal originalPrice;
    private BigDecimal salePrice;
    private Integer initialStock;
    private Integer currentStock;

    public int getSoldPercentage() {
        if (initialStock == null || initialStock == 0) return 100;
        return (int) (((double) (initialStock - currentStock) / initialStock) * 100);
    }
    public static ProductResponse fromProduct(Product product) {
        if (product == null) return null;
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .image(product.getImage())
                .originalPrice(product.getOriginalPrice())
                .salePrice(product.getSalePrice())
                .initialStock(product.getInitialStock())
                .currentStock(product.getCurrentStock())
                .build();
    }
}
