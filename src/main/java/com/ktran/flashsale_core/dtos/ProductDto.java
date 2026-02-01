package com.ktran.flashsale_core.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private String name;
    private String image;
    private BigDecimal price;
    private BigDecimal salePrice;
    private Integer initialStock;
    private Integer currentStock;

}
