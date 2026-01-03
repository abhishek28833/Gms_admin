package com.admin.service.keycloak;

import com.admin.controller.base.BaseController;
import com.admin.controller.base.GlobalApiResponse;
import com.admin.model.dto.SignInRequestDto;
import com.admin.model.dto.SignUpRequestDto;
import com.admin.model.response.KeycloakTokenResponse;
import com.admin.utils.CustomApiCalls;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Slf4j
@Service
public class KeycloakUserService implements KeycloakUserServiceInterface {

    @Value("${keycloak.baseUrl}")
    private String keycloakBaseUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client_id}")
    private String clientId;

    @Value("${keycloak.client_secret}")
    private String clientSecret;

    @Value("${keycloak.client_uuid}")
    private String clientUuid;

    @Value("${keycloak.roles.user}")
    private String userRole;

    @Value("${keycloak.roles.owner}")
    private String ownerRole;

    private final RestTemplate restTemplate = new RestTemplate();
    private final CustomApiCalls customApiCalls;
    private BaseController baseController;
    private GlobalApiResponse res;
    ObjectMapper objectMapper = new ObjectMapper();

    public KeycloakUserService(CustomApiCalls customApiCalls, BaseController baseController , WebClient.Builder webClientBuilder) {
        this.customApiCalls = customApiCalls;
        this.baseController = baseController;
    }


    private String getKeyclockadminToken(){
        try{
            String url = keycloakBaseUrl + "/realms/" + realm + "/protocol/openid-connect/token";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
//            JsonObject requestBody = new JsonObject();
            requestBody.add("grant_type","client_credentials");
            requestBody.add("client_id",clientId);
            requestBody.add("client_secret",clientSecret);
            requestBody.add("scope","openid profile email");
            GlobalApiResponse apiResponse = customApiCalls.makePostRequest(url,requestBody,headers);
            if(!apiResponse.isStatus()){
                log.info("GetKeycloak token api fails. ");
                throw new RuntimeException("GetKeycloak token api fails. ");
            }

            String responseString = apiResponse.getData().toString();;

            JsonNode accessToken;
            try {
                accessToken = objectMapper.readTree(responseString);
            }catch (JsonProcessingException e){
                throw new RuntimeException("Json Process exception.");
            }
            if(accessToken.get("access_token") != null) return accessToken.get("access_token").asText();
            else throw new RuntimeException("No access Token Found. ");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public GlobalApiResponse createUser(SignUpRequestDto requestDto){
        String url = keycloakBaseUrl + "/admin/realms/" + realm + "/users";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getKeyclockadminToken());


        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("username", requestDto.getUsername());
        requestBody.put("firstName", requestDto.getFirstName());
        requestBody.put("lastName", requestDto.getLastName());
        requestBody.put("email", requestDto.getEmail());
        requestBody.put("emailVerified", requestDto.getEmailVarified());
        requestBody.put("enabled", true);

        // Attributes
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("mobile.", Collections.singletonList("+91" + requestDto.getUsername()));
        attributes.put("otpVerified", Collections.singletonList("false"));
        attributes.put("registrationDate", Collections.singletonList("2025-03-26T12:00:00Z"));
        attributes.put("createdByAdmin", Collections.singletonList("true"));
        requestBody.put("attributes", attributes);

        // Credentials
        Map<String, Object> credentials = new HashMap<>();
        credentials.put("type", "password");
        credentials.put("value", requestDto.getPassword());
        credentials.put("temporary", requestDto.getTemporary());
        requestBody.put("credentials", Collections.singletonList(credentials));

        GlobalApiResponse response = customApiCalls.makePostRequest(url,requestBody,headers);

        if(!response.isStatus()){
            throw new RuntimeException("user is notcreated some error occured");
        }
        return response;
    }


    @Transactional
    public JsonNode registerUser(SignUpRequestDto requestDto, String role) throws JsonProcessingException {

        createUser(requestDto);

        GlobalApiResponse response = getUserId(requestDto.getUsername());

        log.info("response of userDetail: " + response.toString());
        String jsonString = response.getData().toString();
        JsonNode jsonNode = objectMapper.readTree(jsonString);

        if(jsonNode.has("error")){
            return jsonNode;
        }

        String userId = jsonNode.get(0).get("id").asText();

        String roleId = getRoleIdByName(role).toString();

        assignRole(userId,roleId,role);
        return jsonNode;
    }

    public List<JsonNode> getAssignedRolesOfUser(String userId){
        String url = keycloakBaseUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mapping/realm";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getKeyclockadminToken());
        GlobalApiResponse response = customApiCalls.makeGetRequest(url,headers);
        if(!response.isStatus()){
            log.info("Unable to get role details of userId :"+ userId);
            throw new RuntimeException("Unable to get role details. ");
        }

        String responseString = response.getData().toString();;

        List<JsonNode> roleData;
        try {
            roleData = (List<JsonNode>) objectMapper.readTree(responseString);
        }catch (JsonProcessingException e){
            throw new RuntimeException("Json Process exception.");
        }
        return roleData;
    }



    public String getRoleIdByName(String roleName){
        String url = keycloakBaseUrl + "/admin/realms/" + realm + "/roles/" + roleName;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getKeyclockadminToken());
        GlobalApiResponse response = customApiCalls.makeGetRequest(url,headers);
        if(!response.isStatus()){
            log.info("Unable to get role details. ");
            throw new RuntimeException("Unable to get role details. ");
        }

        String responseString = response.getData().toString();;

        JsonNode roleData;
        try {
            roleData = objectMapper.readTree(responseString);
        }catch (JsonProcessingException e){
            throw new RuntimeException("Json Process exception.");
        }
        return roleData.get("id").asText();

    }

    private boolean assignRole(String userId, String roleId, String roleName){
        String url =  keycloakBaseUrl +  "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getKeyclockadminToken());
//        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("id", roleId);
        requestBody.put("name",roleName);
        List<Map<String, Object>> roles = Collections.singletonList(requestBody);
        GlobalApiResponse response = customApiCalls.makePostRequest(url,roles,headers);
        if(!response.isStatus()){
            log.info("Unable to assign the role for the User with userId :" + userId);
            return false;
        }
        return true;

    }

    public GlobalApiResponse getUserId(String userName) {
        String url = keycloakBaseUrl + "/admin/realms/" + realm + "/users?username=" + userName + "&exact=true";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getKeyclockadminToken());

        GlobalApiResponse response = customApiCalls.makeGetRequest(url,headers);
        if(!response.isStatus()){
            log.info("GetKeycloak userId api fails. ");
            throw new RuntimeException("GetKeycloak userId api fails.  ");
        }

        return response;

    }

    public JsonNode userSignin(SignInRequestDto signInRequestDto){
        log.info("**************************** Inside the User Login service ****************************");
        String url = keycloakBaseUrl + "/realms/" + realm + "/protocol/openid-connect/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type","password");
        requestBody.add("client_id",clientId);
//        requestBody.add("client_secret",clientSecret);
        requestBody.add("username",signInRequestDto.getUsername());
        requestBody.add("password",signInRequestDto.getPassword());
//        requestBody.add("scope","openid profile email");
        log.info("Going To hit API POST Url: " + url + " Headers: " + headers + " Body : " + requestBody);
        GlobalApiResponse apiResponse = customApiCalls.makePostRequest(url,requestBody,headers);

        if(!apiResponse.isStatus()){
            log.info("Signin api fails. ");
            throw new RuntimeException("Signin api fails. ");
        }

        String responseString = apiResponse.getData().toString();;

        JsonNode response;
        try {
            response = objectMapper.readTree(responseString);
        }catch (JsonProcessingException e){
            throw new RuntimeException("Json Process exception.");
        }
        return response;
    }

    public Object getUserInfoFromToken(String token) {
        try{
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Authorization", "Bearer " + token);
            String baseUrl = keycloakBaseUrl + "/realms/" + realm + "/protocol/openid-connect/userinfo";
            ResponseEntity responseEntity = restTemplate
                    .exchange(baseUrl, HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class);
            JsonObject response = new Gson().fromJson(responseEntity.getBody().toString(), JsonObject.class);
            log.info("Response : "+ response.toString());

            if(response.has("error")){
                return null;
            }
            return response;
        }
        catch (Exception e)
        {
            return null;
        }
    }


    public KeycloakTokenResponse login(String username, String password) {

        try {
            String url = keycloakBaseUrl + "/realms/" + realm + "/protocol/openid-connect/token";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("grant_type","password");
            requestBody.add("client_id",clientId);
            requestBody.add("username",username);
            requestBody.add("password",password);
//        requestBody.add("client_secret",clientSecret);
//        requestBody.add("scope","openid profile email");
            GlobalApiResponse apiResponse = customApiCalls.makePostRequest(url,requestBody,headers);
            if(!apiResponse.isStatus()){
                log.info("GetKeycloak token api fails. ");
                throw new RuntimeException("GetKeycloak token api fails. ");
            }

            String responseString = apiResponse.getData().toString();
            JsonNode accessToken = objectMapper.readTree(responseString);
            return objectMapper.convertValue(accessToken, KeycloakTokenResponse.class);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public KeycloakTokenResponse refreshToken(String refreshToken) {
        String tokenUrl = keycloakBaseUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", clientId);
        body.add("refresh_token", refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        GlobalApiResponse apiResponse = customApiCalls.makePostRequest(tokenUrl,body,headers);
        if(!apiResponse.isStatus()){
            log.info("GetKeycloak token from refresh token api fails. ");
            throw new RuntimeException("GetKeycloak token from refresh token api fails. ");
        }

        String responseString = apiResponse.getData().toString();;

        JsonNode tokenData;
        try {
            tokenData = objectMapper.readTree(responseString);
        }catch (JsonProcessingException e){
            throw new RuntimeException("Json Process exception.");
        }

        if (tokenData.has("error")) {
            String errorMessage = tokenData.has("error_description") ? tokenData.get("error_description").asText() : "Refresh token failed";
            throw new RuntimeException(errorMessage);
        }
        KeycloakTokenResponse response = objectMapper.convertValue(tokenData, KeycloakTokenResponse.class);
        log.info("response for refreshToke " + response);
        return response;
    }

    public void logout(String refreshToken) {

        String logoutUrl = keycloakBaseUrl + "/realms/" + realm + "/protocol/openid-connect/logout";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("refresh_token", refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        GlobalApiResponse apiResponse = customApiCalls.makePostRequest(logoutUrl,body,headers);
        if(!apiResponse.isStatus()){
            log.info("logout api fails. ");
            throw new RuntimeException("Logout api fails. ");
        }

    }
}
