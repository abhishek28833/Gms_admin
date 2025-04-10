package com.admin.utils;

import com.admin.controller.base.BaseController;
import com.admin.controller.base.GlobalApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Configuration
public class CustomApiCalls extends BaseController {

    @Autowired
    private RestTemplate restTemplate;

    public GlobalApiResponse makePostRequest(String url, Object body, HttpHeaders headers) {
        log.info("Making a POST request to URL: {}, Headers: {}, Body: {}", url, headers, body);
        try {
            HttpEntity<Object> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            log.info("Response Status Code: {}", response.getStatusCode());
            return successResponse("Success", response.getBody());
        } catch (Exception e) {
            log.error("Error during POST request to {}: {}", url, e.getMessage(), e);
            return errorResponse(e.getMessage(), body);
        }
    }

    public GlobalApiResponse makeGetRequest(String url, HttpHeaders headers) {
        log.info("Making a GET request to URL: {}, Headers: {}", url, headers);
        try {
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            log.info("Response Status Code: {}", response.getStatusCode());
            return successResponse("Success", response.getBody());
        } catch (Exception e) {
            log.error("Error during GET request to {}: {}", url, e.getMessage(), e);
            return errorResponse(e.getMessage(), null);
        }
    }
}
