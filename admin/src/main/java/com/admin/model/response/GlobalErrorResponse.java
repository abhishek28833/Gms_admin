package com.admin.model.response;

import java.util.Arrays;
import java.util.List;

public class GlobalErrorResponse {
    private Boolean status;
    private List<ErrorResponse> errors;

    public GlobalErrorResponse(Boolean status,ErrorResponse errorMessage){
        this.status = status;
        errors = Arrays.asList(errorMessage);
    }
}

