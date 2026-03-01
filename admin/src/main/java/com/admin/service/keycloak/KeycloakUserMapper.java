package com.admin.service.keycloak;

import com.admin.model.dto.CredentialRepresentationDto;
import com.admin.model.dto.KeycloakUserRepresentationDto;
import com.admin.model.dto.SignUpRequestDto;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class KeycloakUserMapper {

    public KeycloakUserRepresentationDto toUserRepresentation(SignUpRequestDto request) {

        // Format registration date in ISO format
        String registrationDate = DateTimeFormatter.ISO_INSTANT
                .format(Instant.now());

        // Create attributes map
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("mobile.", List.of("+91" + request.getUsername()));
        attributes.put("otpVerified", List.of("true"));
        attributes.put("registrationDate", List.of(registrationDate));

        // Create credential
        CredentialRepresentationDto credential = CredentialRepresentationDto.builder()
                .type("password")
                .value(request.getPassword())
                .temporary(false)
                .build();

        return KeycloakUserRepresentationDto.builder()
                .username(request.getUsername())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .attributes(attributes)
                .createdTimestamp(Instant.now().toEpochMilli())
                .enabled(true)
                .credentials(List.of(credential))
                .emailVerified(true)
                .build();

    }
}
