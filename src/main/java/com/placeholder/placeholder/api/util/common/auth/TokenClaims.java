package com.placeholder.placeholder.api.util.common.auth;

import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class TokenClaims {
    // Standard JWT claims (RFC 7519)
    public static final String CLAIM_ISSUER = "iss";                // Issuer
    public static final String CLAIM_SUBJECT = "sub";               // Subject (typically uuid)
    public static final String CLAIM_AUDIENCE = "aud";              // Audience
    public static final String CLAIM_EXPIRATION = "exp";            // Expiration time
    public static final String CLAIM_NOT_BEFORE = "nbf";            // Not before
    public static final String CLAIM_ISSUED_AT = "iat";             // Issued at
    public static final String CLAIM_JWT_ID = "jti";                // JWT ID (unique identifier)

    // Custom/OpenID Connect identity claims
    public static final String CLAIM_KEY_PREFERRED_USERNAME = "preferred_username";
    public static final String CLAIM_KEY_EMAIL = "email";
    public static final String CLAIM_KEY_NAME = "name";
    public static final String CLAIM_KEY_GIVEN_NAME = "given_name";
    public static final String CLAIM_KEY_FAMILY_NAME = "family_name";
    public static final String CLAIM_KEY_PICTURE = "picture";

    // Custom authorization claims
    public static final String CLAIM_KEY_ROLE = "role";             // Single role

    private final Map<String, Object> claims;

    /**
     * Retrieves a claim by its key and tries to cast it to the specified type.
     * <p>
     *     Use it when you need to handle additional token claims knowing exactly the type,
     *     otherwise use the predefined methods.
     * </p>
     *
     * @param <T>  the expected type of the claim value
     * @param key  the claim key
     * @param type the Class object of the expected type
     * @return an Optional containing the Claim if present and of the correct type, otherwise empty.
     */
    public <T> Optional<Claim> getClaim(String key, Class<T> type) {
        Object value = claims.get(key);
        if (type.isInstance(value)) {
            return Optional.of(new Claim(key, type.cast(value)));
        }
        return Optional.empty();
    }

    /**
     * Returns all claims as a list of Claim objects.
     *
     * @return a List containing all claims in the token
     */
    public List<Claim> getAllClaims() {
        return claims.entrySet()
                .stream()
                .map(entry -> new Claim(entry.getKey(), entry.getValue()))
                .toList();
    }

    /**
     * Retrieves the 'sub' (subject) claim, which typically represents the user identifier.
     *
     * @return an Optional containing the subject if present
     */
    public Optional<String> getSubject() {
        return getStringClaim(CLAIM_SUBJECT);
    }

    /**
     * Retrieves the 'email' claim.
     * @return an Optional containing the email if present.
     */
    public Optional<String> getEmail() {
        return getStringClaim(CLAIM_KEY_EMAIL);
    }

    /**
     * Retrieves the 'iat' (issued at) claim as an Instant.
     *
     * @return an Optional containing the issued at time if present
     */
    public Optional<Instant> getIssuedAt() {
        return getDateClaim(CLAIM_ISSUED_AT);
    }

    /**
     * Retrieves the 'exp' (expiration date) claim as an Instant.
     *
     * @return an Optional containing the expiration time if present
     */
    public Optional<Instant> getExpiration() {
        return getDateClaim(CLAIM_EXPIRATION);
    }

    /**
     * Retrieves the 'iss' (issuer) claim.
     *
     * @return an Optional containing the issuer if present
     */
    public Optional<String> getIssuer() {
        return getStringClaim(CLAIM_ISSUER);
    }

    /**
     * Retrieves the 'aud' (audience) claim.
     *
     * @return an Optional containing the audience if present
     */
    public Optional<String> getAudience() {
        return getStringClaim(CLAIM_AUDIENCE);
    }

    /**
     * Retrieves the 'nbf' (not before) claim as an Instant.
     *
     * @return an Optional containing the not before time if present
     */
    public Optional<Instant> getNotBefore() {
        return getDateClaim(CLAIM_NOT_BEFORE);
    }

    /**
     * Retrieves the 'jti' (JWT ID) claim.
     *
     * @return an Optional containing the JWT ID if present
     */
    public Optional<String> getJwtId() {
        return getStringClaim(CLAIM_JWT_ID);
    }

    /**
     * Retrieves the 'name' claim.
     *
     * @return an Optional containing the name if present
     */
    public Optional<String> getName() {
        return getStringClaim(CLAIM_KEY_NAME);
    }


    /**
     * Retrieves the 'preferred_username' claim.
     *
     * @return an Optional containing the preferred username if present
     */
    public Optional<String> getPreferredUsername() {
        return getStringClaim(CLAIM_KEY_PREFERRED_USERNAME);
    }


    /**
     * Retrieves the 'role' claim.
     *
     * @return an Optional containing the user role if present and valid
     */
    public Optional<String> getRole() {
        Object value = claims.get(CLAIM_KEY_ROLE);
        if (value instanceof String) {
            return Optional.of((String) value);
        }
        return Optional.empty();
    }

    /**
     * Helper method to retrieve a claim as a String.
     *
     * @param key the claim key
     * @return an Optional containing the String value if present and of type String.
     */
    private Optional<String> getStringClaim(String key) {
        Object value = claims.get(key);
        if (value instanceof String s) return Optional.of(s);
        return Optional.empty();
    }

    /**
     * Helper method to retrieve a claim as an Instant.
     * Assumes the claim is stored as a numeric epoch seconds value.
     *
     * @param key the claim key
     * @return an Optional containing the Instant if present and valid
     */
    private Optional<Instant> getDateClaim(String key) {
        Object value = claims.get(key);
        if (value instanceof Number n) {
            return Optional.of(Instant.ofEpochSecond(n.longValue()));
        }
        return Optional.empty();
    }
}
