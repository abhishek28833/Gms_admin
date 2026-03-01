package com.admin.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeycloakUserRepresentationDto {

    private String id;

    private String username;
    private String firstName;
    private String lastName;
    private String email;

    private Boolean enabled;
    private Boolean emailVerified;

    private Long createdTimestamp;

    private Map<String, List<String>> attributes;

    private List<CredentialRepresentationDto> credentials;
}