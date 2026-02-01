package com.ktran.flashsale_core.controllers;

import com.ktran.flashsale_core.dtos.OrderDto;
import com.ktran.flashsale_core.responses.ApiResponse;
import com.ktran.flashsale_core.responses.OrderResponse;
import com.ktran.flashsale_core.services.OrderService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/orders")

public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ApiResponse<OrderResponse> createOrder(@RequestBody OrderDto dto) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.createOrder(dto))
                .build();
    }
    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getOrderById(@PathVariable Long id) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.getOrderById(id))
                .build();
    }
    @GetMapping("/user/{userId}")
    public ApiResponse<List<OrderResponse>> getMyOrders(@PathVariable String userId) {
        return ApiResponse.<List<OrderResponse>>builder()
                .result(orderService.getOrderByUserId(userId))
                .build();
    }

    @PutMapping("/{id}/status")
    public ApiResponse<String> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String note
    ) {
        orderService.updateStatus(id, status, note);
        return ApiResponse.<String>builder()
                .result("Ok")
                .build();
    }
}
