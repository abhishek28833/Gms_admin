package com.admin.config.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class InternalServiceInterceptor implements HandlerInterceptor {

    @Value("${other.authKey}")
    private String authKey;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception{
        if(authKey.equals(request.getHeader("auth-key"))){
            return true;
        }else{
            response.setStatus(401);
            response.sendError(401,"Not Authorised");
            return false;
        }

    }

}
