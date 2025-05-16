package com.placeholder.placeholder.api.util.common.auth.internal;

import com.placeholder.placeholder.api.util.common.auth.UserDetailsService;
import com.placeholder.placeholder.api.util.common.auth.base.JWTTokenGenerator;
import com.placeholder.placeholder.api.util.common.auth.base.JwtFacade;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;


@Service
@Primary
@ConditionalOnProperty(
        name = "jwt.provider",
        havingValue = "internal",
        matchIfMissing = true
)
@RequiredArgsConstructor
public class InternalJwtService implements JwtFacade, JWTTokenGenerator {
    private final UserDetailsService userDetailsService;
    private final DefaultJwtTokenGenerator defaultJwtTokenGenerator;
    private final Key secretKey;

    @Override
    public boolean isValidToken(String token) {
        try {
            Date expiration = extractExpiration(token);
            if (expiration.before(new Date())) {
                return false;
            }

            String subject = extractSubject(token);

            // Instantiates the user's details, because if the user does not exist it will throw an exception bigger than my anxiety.
            UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
            return true;

        } catch (JwtException | IllegalArgumentException | UsernameNotFoundException e) {
            return false;
        }
    }

    @Override
    public String extractSubject(String token) {
        return extractClaim(token, Claims.SUBJECT, String.class);
    }

    @Override
    public <T> T extractClaim(String token, String key, Class<T> clazz) {
        Claims claims = parseClaims(token);

        Object value = claims.get(key);
        if (value == null) return null;

        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        }

        if (clazz == Date.class && value instanceof Long) {
            return clazz.cast(new Date((Long) value));
        }

        throw new IllegalArgumentException("Unsupported claim type conversion");
    }

    @Override
    public Map<String, Object> extractClaims(String token) {
        return parseClaims(token);
    }

    @Override
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims.EXPIRATION, Date.class);
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public String generateToken(String subject, Map<String, Object> claims) {
        return defaultJwtTokenGenerator.generateToken(subject, claims);
    }
}
