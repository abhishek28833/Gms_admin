package com.admin.utils;

public class RedisKeys {

    public static String signup(String identifier) {
        return "signup:" + identifier;
    }

    public static String emailOtp(String email) {
        return "otp:email:" + email;
    }

    public static String phoneOtp(String phone) {
        return "otp:phone:" + phone;
    }
}

