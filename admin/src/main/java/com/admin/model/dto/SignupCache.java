package com.admin.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignupCache {
    private SignUpRequestDto signupData;
    private boolean emailVerified;
    private boolean phoneVerified;
}
