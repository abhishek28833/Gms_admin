package com.admin.model.dto;

import lombok.Data;

@Data
public class VerifyOtpRequest {
    private String email;
    private String phone;
    private String emailOtp;
    private String phoneOtp;
}
