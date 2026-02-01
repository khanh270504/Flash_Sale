package com.ktran.flashsale_core.controllers;

import com.ktran.flashsale_core.dtos.PaymentCallBackDto;
import com.ktran.flashsale_core.dtos.PaymentDto;
import com.ktran.flashsale_core.responses.ApiResponse;
import com.ktran.flashsale_core.responses.PaymentResponse;
import com.ktran.flashsale_core.services.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    public ApiResponse<PaymentResponse> createPayment(@RequestBody PaymentDto dto) {
        return ApiResponse.<PaymentResponse>builder()
                .result(paymentService.createPayment(dto))
                .build();
    }

    @PostMapping("/callback")
    public ApiResponse<PaymentResponse> paymentCallBack(@RequestBody PaymentCallBackDto dto) {
        return ApiResponse.<PaymentResponse>builder()
                .result(paymentService.processPayment(dto))
                .build();
    }
    @GetMapping("/vnpay-callback")
    public ApiResponse<PaymentResponse> vnPayCallback(HttpServletRequest request) {
        String status = request.getParameter("vnp_ResponseCode");
        String paymentIdStr = request.getParameter("vnp_TxnRef");

        log.info("VNPAY Callback - Status: {}, PaymentId: {}", status, paymentIdStr);

        try {
            Long paymentId = Long.parseLong(paymentIdStr);


            PaymentCallBackDto dto = PaymentCallBackDto.builder()
                    .paymentId(paymentId)
                    .success("00".equals(status))
                    .build();

            return ApiResponse.<PaymentResponse>builder()
                    .result(paymentService.processPayment(dto))
                    .build();

        } catch (NumberFormatException e) {
            log.error("Lỗi định dạng Payment ID: {}", paymentIdStr);
            return ApiResponse.<PaymentResponse>builder()
                    .code(9999)
                    .message("Lỗi dữ liệu callback từ VNPAY")
                    .build();
        }
    }
    @GetMapping()
    public ApiResponse<List<PaymentResponse>> getAllPayments() {
        return ApiResponse.<List<PaymentResponse>>builder()
                .result(paymentService.getAllPayments())
                .build();
    }
}
