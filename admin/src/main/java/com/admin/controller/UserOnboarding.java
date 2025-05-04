package com.admin.controller;


import com.admin.controller.base.BaseController;
import com.admin.controller.base.GlobalApiResponse;
import com.admin.model.dto.SignInRequestDto;
import com.admin.model.dto.SignUpRequestDto;
import com.admin.model.entity.CustomerDetailManager;
import com.admin.repository.UserDetailRepo;
import com.admin.service.keycloak.KeycloakUserService;
import com.admin.service.keycloak.KeycloakUserServiceInterface;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserOnboarding {

    @Autowired
    private final KeycloakUserServiceInterface keycloakUserService;
    private UserDetailRepo userDetailRepo;
    private BaseController baseController;
    ObjectMapper objectMapper = new ObjectMapper();

    public UserOnboarding(KeycloakUserService keycloakUserService,UserDetailRepo userDetailRepo,BaseController baseController) {
        this.keycloakUserService = keycloakUserService;
        this.userDetailRepo = userDetailRepo;
        this.baseController = baseController;
    }


    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignUpRequestDto requestDto) {
        try {
            String role = "user";
            JsonNode jsonNode = keycloakUserService.registerUser(requestDto,role);

            if(jsonNode.has("error")){
                String errorMessage = jsonNode.get("error").asText();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error during signup: " + errorMessage);
            }
            String userId = jsonNode.get(0).get("id").asText();
            JsonNode values = jsonNode.get(0);
            CustomerDetailManager customerDetailManager = CustomerDetailManager.builder()
                    .userId(userId)
                    .firstName(requestDto.getFirstName())
                    .lastName(requestDto.getLastName())
                    .email(requestDto.getEmail())
                    .username(requestDto.getUsername())
                    .address(requestDto.getAddress())
                    .roleType(1)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            CustomerDetailManager res = userDetailRepo.save(customerDetailManager);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok("User signup successfully");
    }

    @PostMapping("/login")
    public GlobalApiResponse signin(@Valid @RequestBody SignInRequestDto signInRequestDto, HttpServletResponse response){
        JsonNode tokenData = keycloakUserService.userSignin(signInRequestDto);
        if(tokenData.has("error")){
            return baseController.errorResponse(tokenData.get("error_description").asText(),"RP-1");
        }

        String accessToken = tokenData.get("access_token").asText();
        String refreshToken = tokenData.get("refresh_token").asText();

        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofMinutes(15))
                .sameSite("Strict")
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        CustomerDetailManager customerDetailManager = userDetailRepo.getUserData(signInRequestDto.getUsername());

        Map<String, Object> data = new HashMap<>();
        data.put("userId",customerDetailManager.getUserId());
        data.put("first_name",customerDetailManager.getFirstName());
        data.put("last_name",customerDetailManager.getLastName());
        data.put("username",customerDetailManager.getUsername());
        data.put("email",customerDetailManager.getEmail());
        data.put("address",customerDetailManager.getAddress());
        data.put("role",userDetailRepo.getRoleById(customerDetailManager.getRoleType()));
        return baseController.successResponse("ok", data,200);

    }

}
