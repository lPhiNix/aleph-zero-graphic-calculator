package com.placeholder.placeholder.api.util.common.auth.internal;

import com.placeholder.placeholder.api.util.common.auth.base.JWTTokenGenerator;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "jwt.provider", havingValue = "internal", matchIfMissing = true)
public class DefaultJwtTokenGenerator implements JWTTokenGenerator {
    private final Key secretKey;

    public DefaultJwtTokenGenerator(Key secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public String generateToken(String subject, Map<String, Object> claims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + getDefaultExpirationMillis());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
