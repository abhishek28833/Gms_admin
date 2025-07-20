package com.admin.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalErrorResponse {
    private Boolean status;
    private List<ErrorResponse> errors;

    public GlobalErrorResponse(Boolean status,ErrorResponse errorMessage){
        this.status = status;
        errors = Arrays.asList(errorMessage);
    }
}

