package com.admin.controller.base;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalApiResponse {
    private Integer statusCode;
    private boolean status;
    private String message;
    private Object data;
}
