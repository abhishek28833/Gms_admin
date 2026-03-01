package com.admin.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CredentialRepresentationDto {

    private String type;        // password
    private String value;       // actual password
    private Boolean temporary;  // false
}