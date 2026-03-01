package com.admin.config;

import com.admin.service.keycloak.Keycloak;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakAdminConfig {

    @Value("${keycloak.baseUrl}")
    private String keycloakBaseUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client_id}")
    private String clientId;

    @Value("${keycloak.client_secret}")
    private String clientSecret;


    private String grantType = "password";
    private String scope = "openid profile email";

    @Bean
    public Keycloak keycloak() {
        return Keycloak.builder()
                .serverUrl(keycloakBaseUrl)
                .client_id(clientId)
                .client_secret(clientSecret)
                .realm(realm)
                .scope(scope)
                .grant_type(grantType)
                .build();
    }
}

