package com.admin.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SignInRequestDto {

    @NotBlank(message = "Username (mobile number) is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Username must be a valid 10-digit mobile number")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;
}
