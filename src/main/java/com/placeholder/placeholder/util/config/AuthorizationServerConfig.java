package com.placeholder.placeholder.util.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.placeholder.placeholder.api.user.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Configuration
@EnableWebSecurity
public class AuthorizationServerConfig {

    /**
     * Configures the registered client repository for the OAuth2 Authorization Server.
     * <p>
     * This bean defines a single registered client with the specified client ID, name,
     * authentication methods, authorization grant types, and redirect URIs.
     * </p>
     *
     * @param clientProps the properties for the OAuth2 client
     * @return the configured {@link RegisteredClientRepository}
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository(OAuth2ClientProperties clientProps) {
        RegisteredClient.Builder builder = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(clientProps.getId())
                .clientName(clientProps.getName())
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(false) // STFU Consent Screen.
                        .build())
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri(clientProps.getRedirectUri());

        clientProps.getScopes().forEach(builder::scope);

        return new InMemoryRegisteredClientRepository(builder.build());
    }


    /**
     * Configures the RSA key pair used for signing JWT tokens in the OAuth2 Authorization Server.
     * <p>
     * This bean generates a new RSA key pair with a key size of 2048 bits.
     * The private key is used to sign JWT tokens, while the public key is used to verify them.
     * </p>
     *
     * @return the generated {@link KeyPair}
     */
    @Bean
    public KeyPair keyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Configures the JWK source for the OAuth2 Authorization Server.
     * <p>
     * This bean provides the public and private keys used for signing and verifying JWT tokens.
     * It creates an RSA key pair and wraps it in a JWK set.
     * </p>
     *
     * @param keyPair the RSA key pair to be used
     * @return the configured {@link JWKSource}
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource(KeyPair keyPair) {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    /**
     * Configures the JWT decoder for the OAuth2 Authorization Server.
     * <p>
     * This bean is used to decode JWT tokens issued by the authorization server.
     * It uses the {@link JWKSource} to retrieve the public keys for signature verification.
     * </p>
     *
     * @param jwkSource the JWK source containing the public keys
     * @return the configured {@link JwtDecoder}
     */
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    /**
     * Configures the authorization server settings.
     * <p>
     * This bean is used to customize the settings of the OAuth2 Authorization Server,
     * such as token endpoint, authorization endpoint, and more.
     * </p>
     *
     * @return the configured {@link AuthorizationServerSettings}
     */
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    /**
     * Configures the password encoder to use BCrypt hashing.
     *
     * @return the configured {@link PasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer(UserService userService) {
        return context -> {
            if (context.getTokenType().getValue().equals("access_token")) {
                Authentication principal = context.getPrincipal();

                if (principal.getPrincipal() instanceof UserDetails userDetails) {
                    // load user details from the database
                    com.placeholder.placeholder.db.models.User user =
                            userService.findUserByIdentifier(userDetails.getUsername());

                    if (user != null) {
                        context.getClaims().subject(user.getPublicId());
                        context.getClaims().claim("preferred_username", user.getUsername());
                        context.getClaims().claim("email", user.getEmail());
                        context.getClaims().claim("role", user.getRole().getName());
                    }
                }
            }
        };
    }

}
