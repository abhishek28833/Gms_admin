package com.admin.service.keycloak;

import com.admin.constants.StringConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Value("${keycloak.client_id}")
    private String clientId;


    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {

        Collection<GrantedAuthority> authorities = new ArrayList<>();

        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");

        if (resourceAccess == null) {
            return authorities;
        }


        Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get(clientId);

        if (clientAccess == null) {
            return authorities;
        }

        List<String> roles = (List<String>) clientAccess.get("roles");

        if (roles == null) {
            return authorities;
        }

        roles.forEach(role ->
                authorities.add(new SimpleGrantedAuthority(role))
        );

        return authorities;
    }
}
