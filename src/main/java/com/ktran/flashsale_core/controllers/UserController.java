package com.ktran.flashsale_core.controllers;

import com.ktran.flashsale_core.dtos.VerifyDTO;
import com.ktran.flashsale_core.entities.User;
import com.ktran.flashsale_core.responses.ApiResponse;
import com.ktran.flashsale_core.responses.UserResponse;
import com.ktran.flashsale_core.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    public ApiResponse<String> login(@RequestBody User user) {
        return ApiResponse.<String>builder()
                .result(userService.login(user))
                .build();
    }
    @PostMapping("/verify")
    public ApiResponse<UserResponse> verify(
            @RequestBody VerifyDTO verifyDTO) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.verifyOTP(verifyDTO))
                .build();
    }

}
