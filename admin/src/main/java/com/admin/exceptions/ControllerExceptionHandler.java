package com.admin.exceptions;

import com.admin.model.response.ErrorResponse;
import com.admin.model.response.GlobalErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.LinkedHashMap;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    private final YamlMessageSource errorCodeMessageSource;

    public ControllerExceptionHandler(@Qualifier("errorCodeMessageSource") YamlMessageSource errorCodeMessageSource) {
        this.errorCodeMessageSource = errorCodeMessageSource;
    }

    @ExceptionHandler(GmsExceptionHandler.class)
    public ResponseEntity<GlobalErrorResponse> gmsExceptionHandler(GmsExceptionHandler ex, WebRequest request) {
        String errorCode = ex.getErrorCode();
        String errorMessage = ex.getMessage(); // default message from exception

        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR; // default in case YAML fails

        try {
            LinkedHashMap<String, Object> messages = errorCodeMessageSource.getMessages(errorCode);
            if (messages != null) {
                if (messages.get("message") != null && (errorMessage == null || errorMessage.isEmpty())) {
                    errorMessage = messages.get("message").toString();
                }
                if (messages.get("http-status") != null) {
                    httpStatus = HttpStatus.valueOf(Integer.parseInt(messages.get("http-status").toString()));
                }
            }
        } catch (Exception e) {
            log.error("Failed to fetch error message for code: " + errorCode, e);
        }

        ErrorResponse errorResponse = new ErrorResponse(errorCode, errorMessage);
        GlobalErrorResponse globalErrorResponse = new GlobalErrorResponse(false, errorResponse);

        log.warn("Handled GmsExceptionHandler: {}", globalErrorResponse);

        return new ResponseEntity<>(globalErrorResponse, httpStatus);
    }
}
