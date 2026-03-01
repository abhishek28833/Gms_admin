package com.admin.service.keycloak;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Builder
@Data
public class Keycloak {
    private final String serverUrl;
    private final String realm;
    private final String client_id;
    private final String client_secret;
    private final String grant_type;
    private final String scope;
}
