package com.admin.exceptions;

import lombok.Getter;

@Getter
public class GmsExceptionHandler extends RuntimeException{
    private final String errorCode;

    public GmsExceptionHandler(String message,String errorCode){
        super(message);
        this.errorCode = errorCode;
    }

    public GmsExceptionHandler(String errorCode){
        super();
        this.errorCode = errorCode;
    }


}

