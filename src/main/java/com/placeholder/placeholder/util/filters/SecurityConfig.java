package com.placeholder.placeholder.util.filters;

import com.placeholder.placeholder.util.exceptions.CustomAccessDeniedHandler;
import com.placeholder.placeholder.util.exceptions.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.util.Set;


/**
 * Security configuration class for defining security filter chains and authentication mechanisms.
 * TODO: refactor this monstro.
 */
@Profile("default")
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * Configures the security filter chain for the OAuth2 Authorization Server.
     *
     * @param http the {@link HttpSecurity} to configure
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfigurer authServer = OAuth2AuthorizationServerConfigurer.authorizationServer();

        http
                .cors(Customizer.withDefaults()) // Enable default CORS configuration
                .securityMatcher(authServer.getEndpointsMatcher()) // Apply this filter chain to authorization server endpoints
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated()) // Require authentication for all requests
                .with(authServer, configurer -> {
                    configurer.oidc(Customizer.withDefaults()); // Enable OpenID Connect 1.0
                })
                .exceptionHandling(exceptions -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"), // Redirect to login page for HTML requests
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )
                );

        return http.build();
    }

    /**
     * Configures the security filter chain for form-based login.
     *
     * @param http the {@link HttpSecurity} to configure
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 1)
    public SecurityFilterChain formLoginSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/login", "/register", "/login_process", "/css/**", "/js/**", "/images/**") // Apply this filter chain to specific endpoints
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()) // Allow all requests to these endpoints
                .formLogin(form -> form
                        .loginPage("/login") // Custom login page
                        .loginProcessingUrl("/login_process") // Endpoint for processing login requests
                        .failureUrl("/login?error=true") // Redirect to login page with error on failure
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // Endpoint for logout
                        .logoutSuccessUrl("/login?logout=true") // Redirect to login page on successful logout
                );

        return http.build();
    }

    /**
     * Configures the security filter chain for the resource server.
     *
     * @param http                     the {@link HttpSecurity} to configure
     * @param allowedEndpoints         a set of endpoints that are publicly accessible
     * @param allowedStaticEndpoints   a set of static resource endpoints that are publicly accessible
     * @param accessDeniedHandler      a custom handler for access denied exceptions
     * @param authenticationEntryPoint a custom entry point for authentication exceptions
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   Set<String> allowedEndpoints,
                                                   Set<String> allowedStaticEndpoints,
                                                   CustomAccessDeniedHandler accessDeniedHandler,
                                                   CustomAuthenticationEntryPoint authenticationEntryPoint) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF protection
                .cors(Customizer.withDefaults()) // Enable default CORS configuration
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Use stateless sessions
                .authorizeHttpRequests(auth -> {
                    allowedEndpoints.forEach(endpoint ->
                            auth.requestMatchers(endpoint).permitAll() // Allow access to specified endpoints
                    );
                    allowedStaticEndpoints.forEach(staticEndpoint ->
                            auth.requestMatchers(staticEndpoint).permitAll() // Allow access to static resources
                    );
                    auth.anyRequest().authenticated(); // Require authentication for all other requests
                })
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint) // Custom entry point for authentication exceptions
                        .accessDeniedHandler(accessDeniedHandler) // Custom handler for access denied exceptions
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults()) // Enable JWT-based authentication for the resource server
                )
                .build();
    }

    /**
     * Configures the authentication manager with a DAO-based authentication provider.
     *
     * @param userDetailsService the {@link UserDetailsService} to use for retrieving user details
     * @param passwordEncoder    the {@link PasswordEncoder} to use for password encoding
     * @return the configured {@link AuthenticationManager}
     */
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                                                       PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService); // Set the user details service
        provider.setPasswordEncoder(passwordEncoder); // Set the password encoder
        return new ProviderManager(provider);
    }
}