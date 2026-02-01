package com.ktran.flashsale_core.services;

import com.ktran.flashsale_core.dtos.VerifyDTO;
import com.ktran.flashsale_core.entities.User;
import com.ktran.flashsale_core.exceptions.AppException;
import com.ktran.flashsale_core.exceptions.ErrorCode;
import com.ktran.flashsale_core.repositorys.UserRepository;
import com.ktran.flashsale_core.responses.UserResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final StringRedisTemplate redisTemplate;


    public String sendOTP(String userName) {
            var checkUser = userRepository.findByUserName(userName)
                    .orElseThrow(() -> new AppException(ErrorCode.USERNAME_INVALID));
        SecureRandom secureRandom = new SecureRandom();
        String otp = String.valueOf(100000 + secureRandom.nextInt(900000));
        redisTemplate.opsForValue().set("otp:" + userName, otp, 60, TimeUnit.SECONDS);
        return "Ma otp la: " + otp;
    }
    public String login(User userName) {
       var checkUsername = userRepository.findByUserName(userName.getUserName())
               .orElseThrow(() -> new AppException(ErrorCode.USERNAME_INVALID));
       if (!checkUsername.getPassword().equals(userName.getPassword())) {
           throw new AppException(ErrorCode.INVALID_PASSWORD);
       }
       return sendOTP(checkUsername.getUserName());
    }
    public UserResponse verifyOTP(VerifyDTO verifyDTO) {
        String serverOtp = redisTemplate.opsForValue().get("otp:" + verifyDTO.getUserName());
        if(serverOtp == null) {
            throw new AppException(ErrorCode.OTP_EXPIRED);
        }
        if(!serverOtp.equals(verifyDTO.getOTP())) {
            throw new AppException(ErrorCode.OTP_INVALID);
        }
        return UserResponse.builder()
                .username(verifyDTO.getUserName())
                .build();
    }
}
