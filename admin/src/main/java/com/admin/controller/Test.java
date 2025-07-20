package com.admin.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/apok/test")
public class Test {

    @GetMapping("/hello")
    public String testing(){
        return "Hello GMS";
    }
}
