package com.ktran.flashsale_core.services;

import com.ktran.flashsale_core.dtos.ProductDto;
import com.ktran.flashsale_core.entities.Product;
import com.ktran.flashsale_core.exceptions.AppException;
import com.ktran.flashsale_core.exceptions.ErrorCode;
import com.ktran.flashsale_core.repositorys.ProductRepository;
import com.ktran.flashsale_core.responses.ProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public void syncStockToRedis(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_OUT_OF_STOCK));
        String key = "product:stock:" + productId;

        redisTemplate.opsForValue().set(key, product.getCurrentStock());
        log.info("Đã đẩy SP vào Redis {}: Stock = {}", productId, product.getCurrentStock());


    }

    public int getStockFromRedis(Long productId) {
        String key = "product:stock:" + productId;
        Object stock = redisTemplate.opsForValue().get(key);

        if(stock == null) {
            Product product = productRepository.findById(productId).orElse(null);
            if(product != null){
                syncStockToRedis(productId);
                return product.getCurrentStock();
            }
            return 0;
        }
        return Integer.parseInt(stock.toString());
    }


    public ProductResponse createProduct(ProductDto dto) {
        Product product = Product.builder()
                .name(dto.getName())
                .salePrice(dto.getSalePrice())
                .currentStock(dto.getCurrentStock())
                .image(dto.getImage())
                .originalPrice(dto.getPrice())
                .initialStock(dto.getInitialStock())
                .build();
        Product saved = productRepository.save(product);

        syncStockToRedis(saved.getId());
        return ProductResponse.fromProduct(saved);
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponse::fromProduct)
                .collect(Collectors.toList());
    }

    public void syncAllProductsToRedis() {
        List<Product> allProducts = productRepository.findAll();

        for (Product p : allProducts) {
            String key = "product:stock:" + p.getId();
            redisTemplate.opsForValue().set(key, p.getCurrentStock());
            log.info("Đã đồng bộ SP {}: Stock {}", p.getId(), p.getCurrentStock());
        }
        log.info("Đã đồng bộ {} product", allProducts.size());
    }
}
