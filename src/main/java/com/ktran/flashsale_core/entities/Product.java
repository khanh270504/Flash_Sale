package com.ktran.flashsale_core.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String image;


    @Column(name = "original_price", nullable = false)
    private BigDecimal originalPrice;

    @Column(name = "sale_price", nullable = false)
    private BigDecimal salePrice;


    @Column(name = "initial_stock", nullable = false)
    private Integer initialStock;

    @Column(name = "current_stock", nullable = false)
    private Integer currentStock;

    @Version
    private Integer version;
}