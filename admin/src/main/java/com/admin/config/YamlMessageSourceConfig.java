package com.admin.config;

import com.admin.exceptions.YamlMessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class YamlMessageSourceConfig {

    @Bean("errorCodeMessageSource")
    public YamlMessageSource yamlMessageSource(){
        return new YamlMessageSource("error-codes.yml");
    }

}
