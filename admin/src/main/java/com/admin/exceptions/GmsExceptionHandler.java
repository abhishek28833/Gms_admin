package com.admin.exceptions;


public class GmsExceptionHandler extends RuntimeException{
    String errorCode;

    public GmsExceptionHandler(String message,String errorCode){
        super(message);
        this.errorCode = errorCode;
    }

    public GmsExceptionHandler(String errorCode){
        this.errorCode = errorCode;
    }
}

