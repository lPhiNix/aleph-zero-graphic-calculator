package com.alephzero.alephzero.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configuration properties for the stateless JWT authentication system.
 * <p>
 * Binds the {@code app.jwt.*} properties: the signing secret, the access and
 * refresh token lifetimes and the refresh-token cookie settings.
 */
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    /** HMAC secret used to sign/verify the access tokens. Must be at least 256 bits. */
    private String secret;

    /** Lifetime of an access token. Defaults to 15 minutes. */
    private Duration accessTtl = Duration.ofMinutes(15);

    /** Lifetime of a refresh token. Defaults to 7 days. */
    private Duration refreshTtl = Duration.ofDays(7);

    /** Name of the HttpOnly cookie that carries the refresh token. */
    private String cookieName = "refresh_token";

    /** Whether the refresh cookie must be flagged {@code Secure} (true in production/HTTPS). */
    private boolean cookieSecure = false;
}
