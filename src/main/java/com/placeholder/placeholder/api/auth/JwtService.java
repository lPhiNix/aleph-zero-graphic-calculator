package com.placeholder.placeholder.api.auth;

import com.placeholder.placeholder.api.util.common.auth.TokenClaims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.UUID;


/**
 * Class responsible for generating and parsing JWT tokens.
 * It uses a secret key for signing the tokens and can handle expiration times.
 * If no secret is provided, it generates a random HS256 key.
 */
@Service
@RequiredArgsConstructor
public class JwtService {

    private final Key secretKey;
    private final Long expirationMs;
    private final JwtDecoder jwtDecoder;


    /**
     * Generates a JWT token with the specified username, role, and email.
     *
     * @param username the username to include in the token
     * @param role     the role to include in the token
     * @param email    the email to include in the token
     * @return a signed JWT token as a String
     */
    public String generateToken(String username, String role, String email) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(UUID.randomUUID().toString())
                .setIssuer(TokenClaims.CLAIM_ISSUER)
                .setAudience(TokenClaims.CLAIM_AUDIENCE)
                .claim(TokenClaims.CLAIM_KEY_EMAIL, email)
                .claim(TokenClaims.CLAIM_KEY_PREFERRED_USERNAME, username)
                .claim(TokenClaims.CLAIM_KEY_ROLE, role)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Parses the JWT token and extracts the claims.
     *
     * @param token the JWT token to parse
     * @return a TokenClaims object containing the claims from the token
     * @throws JwtException if the token is invalid or cannot be parsed
     */
    public TokenClaims parseClaims(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return new TokenClaims(jwt.getClaims());
        } catch (JwtException e) {
            throw new JwtException(e.getMessage(), e);
        }
    }
}
