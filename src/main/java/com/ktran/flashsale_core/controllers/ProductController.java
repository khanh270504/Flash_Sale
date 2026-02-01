package com.ktran.flashsale_core.controllers;


import com.ktran.flashsale_core.dtos.ProductDto;
import com.ktran.flashsale_core.responses.ApiResponse;
import com.ktran.flashsale_core.responses.ProductResponse;
import com.ktran.flashsale_core.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ApiResponse<List<ProductResponse>> getAllProducts() {
        return ApiResponse.<List<ProductResponse>>builder()
                .result(productService.getAllProducts())
                .build();
    }

    @PostMapping ApiResponse<ProductResponse> createProduct(@RequestBody ProductDto dto) {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.createProduct(dto))
                .build();
    }

    @GetMapping("/id/stock")
    public ApiResponse<Integer> getStock(@PathVariable Long id) {
        return ApiResponse.<Integer>builder()
                .result(productService.getStockFromRedis(id))
                .build();
    }

    @PostMapping("/sync-all")
    public ApiResponse<String> syncAllStock() {
        productService.syncAllProductsToRedis();
        return ApiResponse.<String>builder()
                .message("Đã đồng bộ tất cả tồn kho lên Redis thành công!")
                .result("OK")
                .build();
    }

    @PostMapping("/{id}/sync")
    public ApiResponse<String> syncOne(@PathVariable Long id) {
        productService.syncStockToRedis(id);
        return ApiResponse.<String>builder()
                .message("Đã đồng bộ sản phẩm " + id)
                .result("OK")
                .build();
    }
}
