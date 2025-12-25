package com.admin.controller;


import com.admin.controller.base.BaseController;
import com.admin.controller.base.GlobalApiResponse;
import com.admin.model.dto.LogoutRequestDto;
import com.admin.model.dto.RefreshTokenRequestDto;
import com.admin.model.dto.SignInRequestDto;
import com.admin.model.dto.SignUpRequestDto;
import com.admin.model.entity.CustomerDetailManager;
import com.admin.model.response.KeycloakTokenResponse;
import com.admin.repository.UserDetailRepo;
import com.admin.service.keycloak.KeycloakUserService;
import com.admin.service.keycloak.KeycloakUserServiceInterface;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class UserOnboarding {

    @Autowired
    private KeycloakUserServiceInterface keycloakUserService;
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
    public ResponseEntity<GlobalApiResponse> signin(@Valid @RequestBody SignInRequestDto signInRequestDto, HttpServletResponse response) {

        JsonNode tokenData = keycloakUserService.userSignin(signInRequestDto);

        if (tokenData.has("error")) {
            String errorMessage = tokenData.has("error_description") ? tokenData.get("error_description").asText() : "Authentication failed";

            GlobalApiResponse errorResponse = GlobalApiResponse.builder()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(false)
                    .message(errorMessage)
                    .data(null)
                    .build();

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(errorResponse);
        }
        KeycloakTokenResponse tokenResponse = objectMapper.convertValue(tokenData, KeycloakTokenResponse.class);

        Cookie refreshCookie = new Cookie(
                "refresh_token",
                tokenResponse.getRefreshToken()
        );

        refreshCookie.setHttpOnly(true);   // JS cannot read
        refreshCookie.setSecure(false);    // true in PROD (HTTPS)
        refreshCookie.setPath("/auth");    // only auth APIs
        refreshCookie.setMaxAge(30 * 60);  // 30 minutes

        response.addCookie(refreshCookie);

        // Remove refresh token from response body
        tokenResponse.setRefreshToken(null);

        return ResponseEntity.ok(
                GlobalApiResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .status(true)
                        .message("Login successful")
                        .data(tokenResponse)
                        .build()
        );
    }

    @GetMapping("/me")
    public Map<String, Object> userInfo(@AuthenticationPrincipal Jwt jwt) {
        return jwt.getClaims();
    }

    @GetMapping("/username")
    public String username(@AuthenticationPrincipal Jwt jwt) {
        return jwt.getClaim("preferred_username");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<GlobalApiResponse> refreshToken(HttpServletRequest request, HttpServletResponse response) {

        // Read refresh token from cookie
        String refreshToken = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refresh_token".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(GlobalApiResponse.builder()
                            .statusCode(HttpStatus.UNAUTHORIZED.value())
                            .status(false)
                            .message("Refresh token missing")
                            .data(null)
                            .build());
        }

        // Call Keycloak
        KeycloakTokenResponse tokenResponse = keycloakUserService.refreshToken(refreshToken);

        // Rotate refresh token (IMPORTANT)
        Cookie newRefreshCookie = new Cookie("refresh_token", tokenResponse.getRefreshToken());

        newRefreshCookie.setHttpOnly(true);
        newRefreshCookie.setSecure(false);
        newRefreshCookie.setPath("/auth");
        newRefreshCookie.setMaxAge(30 * 60);

        response.addCookie(newRefreshCookie);

        //Remove refresh token from body
        tokenResponse.setRefreshToken(null);

        return ResponseEntity.ok(
                GlobalApiResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .status(true)
                        .message("Token refreshed")
                        .data(tokenResponse)
                        .build()
        );
    }


    @PostMapping("/logout")
    public ResponseEntity<GlobalApiResponse> logout(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refresh_token".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        if (refreshToken != null) {
            keycloakUserService.logout(refreshToken);
        }

        // Delete cookie
        Cookie deleteCookie = new Cookie("refresh_token", null);
        deleteCookie.setHttpOnly(true);
        deleteCookie.setSecure(false);
        deleteCookie.setPath("/auth");
        deleteCookie.setMaxAge(0); // delete

        response.addCookie(deleteCookie);

        return ResponseEntity.ok(
                GlobalApiResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .status(true)
                        .message("Logged out successfully")
                        .data(null)
                        .build()
        );
    }


}
