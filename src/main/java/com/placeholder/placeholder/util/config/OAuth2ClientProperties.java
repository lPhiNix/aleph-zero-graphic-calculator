package com.placeholder.placeholder.util.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration properties for OAuth2 client settings.
 * <p>
 * This class is used to bind properties prefixed with "oauth2.client" from the application properties file.
 * It includes fields for client ID, name, redirect URI, and scopes.
 * </p>
 */
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
