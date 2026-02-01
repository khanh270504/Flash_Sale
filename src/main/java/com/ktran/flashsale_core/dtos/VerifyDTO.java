package com.ktran.flashsale_core.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class VerifyDTO {
    String userName;
    String OTP;
}
