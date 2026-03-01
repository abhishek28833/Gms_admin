package com.admin.service.otp;

import com.admin.model.dto.OtpCache;
import com.admin.model.dto.SignupCache;
import com.admin.model.dto.VerifyOtpRequest;
import com.admin.service.keycloak.KeycloakUserServiceInterface;
import com.admin.utils.RedisKeys;
import com.admin.utils.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpVerificationService {

    private final RedisService redisService;
    public final KeycloakUserServiceInterface keycloakUserServiceInterface;

    public void verifyOtps(VerifyOtpRequest request) {

        SignupCache cache = redisService.get(RedisKeys.signup(request.getEmail()), SignupCache.class);

        if (cache == null) {
            throw new RuntimeException("Signup session expired. Please signup again.");
        }

        if(!cache.isEmailVerified()) verifyEmailOtp(request.getEmail(), request.getEmailOtp(), cache);

        if(!cache.isPhoneVerified()) verifyPhoneOtp(request.getPhone(), request.getPhoneOtp(), cache);

        if(!cache.isEmailVerified() || !cache.isPhoneVerified()){
            throw new RuntimeException(" kindly verify email and phone both ");
        }

        try {
            keycloakUserServiceInterface.registerUser(cache.getSignupData(),"user");
        } catch (JsonProcessingException e) {
            log.info(" ================ERROR WHILE REGISTER A USER==================================");
            throw new RuntimeException(e);
        }

        redisService.delete(request.getEmail());
    }

    private void verifyEmailOtp(String email, String otp, SignupCache cache) {
        OtpCache stored = redisService.get(RedisKeys.emailOtp(email), OtpCache.class);

        if (stored == null || !stored.getOtp().equals(otp))
            throw new RuntimeException("Invalid Email OTP");

        cache.setEmailVerified(true);
        redisService.delete(RedisKeys.emailOtp(email));
    }

    private void verifyPhoneOtp(String phone, String otp, SignupCache cache) {
        OtpCache stored = redisService.get(RedisKeys.phoneOtp(phone), OtpCache.class);

        if (stored == null || !stored.getOtp().equals(otp))
            throw new RuntimeException("Invalid Phone OTP");

        cache.setPhoneVerified(true);
        redisService.delete(RedisKeys.phoneOtp(phone));
    }
}
