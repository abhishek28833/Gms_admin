package com.admin.config;

import com.admin.config.interceptors.AuthInterceptor;
import com.admin.config.interceptors.InternalServiceInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    private final InternalServiceInterceptor internalServiceInterceptor;
    private final AuthInterceptor authInterceptor;

    public WebConfig(InternalServiceInterceptor internalServiceInterceptor, AuthInterceptor authInterceptor) {
        this.internalServiceInterceptor = internalServiceInterceptor;
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*");
    }

    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(internalServiceInterceptor)
                .addPathPatterns("/pako/**")
                .excludePathPatterns("");

        registry.addInterceptor(authInterceptor)
                .excludePathPatterns("")
                .pathMatcher(new AntPathMatcher());
    }

}
