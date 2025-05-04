package com.admin.exceptions;

import com.admin.model.response.ErrorResponse;
import com.admin.model.response.GlobalErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.LinkedHashMap;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    @Autowired
    @Qualifier("errorCodeMessageSource")
    private final YamlMessageSource errorCodeMessageSource;

    public ControllerExceptionHandler(YamlMessageSource errorCodeMessageSource) {
        this.errorCodeMessageSource = errorCodeMessageSource;
    }

    @ExceptionHandler(GmsExceptionHandler.class)
    public ResponseEntity<GlobalErrorResponse> gmsExceptionHandler(GmsExceptionHandler ex, WebRequest request) {
        LinkedHashMap<String,Object> messages = errorCodeMessageSource.getMessages(ex.errorCode);
        log.error(ex.toString());
        String errorMessage;
        HttpStatus httpStatus ;
        errorMessage = String.valueOf(messages.get("message"));
        if(ex.getMessage()!=null){
            errorMessage=ex.getMessage();
        }
        httpStatus = HttpStatus.valueOf(Integer.parseInt(String.valueOf(messages.get("http-status"))));
        ErrorResponse errorResponse = new ErrorResponse(ex.errorCode,errorMessage);
        GlobalErrorResponse globalErrorResponse = new GlobalErrorResponse(false, errorResponse);

        ResponseEntity<GlobalErrorResponse> res = new ResponseEntity<>(globalErrorResponse,httpStatus);

        log.info("checking errors: " + res.toString());
        return res;
    }
}
