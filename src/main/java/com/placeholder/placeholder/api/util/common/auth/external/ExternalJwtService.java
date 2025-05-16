package com.placeholder.placeholder.api.util.common.auth.external;

import com.placeholder.placeholder.api.util.common.auth.base.JwtFacade;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * Compatible with any provider with OpenID Connect (OIDC)
 */
@Service()
@ConditionalOnProperty(name = {"jwt.provider"}, havingValue = "external")
public class ExternalJwtService implements JwtFacade {

    private final JwtDecoder jwtDecoder;

    /**
     * Class constructor, instanciates the {@link JwtDecoder} class with the variable defined in
     * {@code application.properties}
     */
    public ExternalJwtService(@Value("${jwt.provider.issuer-uri}") String issuerUri) {
        this.jwtDecoder = JwtDecoders.fromOidcIssuerLocation(issuerUri);
    }

    @Override
    public boolean isValidToken(String token) {
        try {
            jwtDecoder.decode(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    @Override
    public String extractSubject(String token) {
        return jwtDecoder.decode(token).getSubject();
    }

    @Override
    public <T> T extractClaim(String token, String key, Class<T> clazz) {
        Object claim = jwtDecoder.decode(token).getClaim(key);
        return clazz.cast(claim);
    }

    @Override
    public Map<String, Object> extractClaims(String token) {
        return jwtDecoder.decode(token).getClaims();
    }

    @Override
    public Date extractExpiration(String token) {
        return Date.from(Objects.requireNonNull(jwtDecoder.decode(token).getExpiresAt()));
    }
}
