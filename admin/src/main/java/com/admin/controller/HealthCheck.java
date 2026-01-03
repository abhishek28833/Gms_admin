package com.admin.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/health")
public class HealthCheck {

    @GetMapping("/check")
    public String healthCheck(){
        log.info(" Health ckeck Success! ");
        return "I am Ok! ";
    }

}
