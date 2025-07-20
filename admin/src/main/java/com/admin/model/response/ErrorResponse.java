package com.admin.model.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private String error_codes;
    private String message;

}
