package com.admin.service.otp;

import com.admin.model.dto.OtpCache;
import com.admin.utils.OtpGenerator;
import com.admin.utils.RedisKeys;
import com.admin.utils.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final RedisService redisService;
    private final OtpGenerator otpGenerator;

    public void sendEmailOtp(String email) {
        String otp = otpGenerator.generate();

        redisService.set(
                RedisKeys.emailOtp(email),
                OtpCache.builder().otp(otp).attempts(0).build(),
                5
        );

        // TODO: integrate email service
        System.out.println("EMAIL OTP: " + otp);
    }

    public void sendPhoneOtp(String phone) {
        String otp = otpGenerator.generate();

        redisService.set(
                RedisKeys.phoneOtp(phone),
                OtpCache.builder().otp(otp).attempts(0).build(),
                5
        );

        // TODO: integrate SMS service
        System.out.println("PHONE OTP: " + otp);
    }
}
