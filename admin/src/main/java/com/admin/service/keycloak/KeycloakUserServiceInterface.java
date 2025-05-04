package com.admin.service.keycloak;

import com.admin.controller.base.GlobalApiResponse;
import com.admin.model.dto.SignInRequestDto;
import com.admin.model.dto.SignUpRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface KeycloakUserServiceInterface {

    public JsonNode registerUser(SignUpRequestDto requestDto, String role) throws JsonProcessingException;

    public List<JsonNode> getAssignedRolesOfUser(String userId);

    public String getRoleIdByName(String roleName);

    public GlobalApiResponse getUserId(String userName);

    public JsonNode userSignin(SignInRequestDto signInRequestDto);

    public Object getUserInfoFromToken(String token);

}
