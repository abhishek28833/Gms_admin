package com.admin.config.interceptors;

import com.admin.exceptions.GmsExceptionHandler;
import com.admin.service.keycloak.KeycloakUserServiceInterface;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final KeycloakUserServiceInterface keycloakUserServiceInterface;

    public AuthInterceptor(KeycloakUserServiceInterface keycloakUserServiceInterface){
        this.keycloakUserServiceInterface = keycloakUserServiceInterface;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader("Authorization")!=null ? request.getHeader("Authorization"): request.getHeader("authorization");

        if(authHeader == null){
            log.info("------> Null Authorization: " + request.getHeaderNames().toString());
            response.setStatus(401);
            throw new GmsExceptionHandler("RP-102");
        }

        String[] authHeaderValue = authHeader.split(" ");
        JsonObject userInfo = (JsonObject) keycloakUserServiceInterface.getUserInfoFromToken(authHeaderValue[0]);
        if(userInfo != null){
            request.setAttribute("userId",userInfo.get("sub").getAsString());
            return true;
        }else{
            response.setStatus(401);
            throw new GmsExceptionHandler("RP-102");
        }
    }

//    @Override
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
//                            @Nullable ModelAndView modelAndView) throws Exception {
//        log.info("response checking " + response.toString());
//        log.info("faltu logging");
//    }

}
