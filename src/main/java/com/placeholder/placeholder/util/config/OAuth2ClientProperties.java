package com.placeholder.placeholder.util.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "oauth2.client")
public class OAuth2ClientProperties {
    // Getters and Setters
    private String id;
    private String name;
    private String redirectUri;
    private List<String> scopes;

}
