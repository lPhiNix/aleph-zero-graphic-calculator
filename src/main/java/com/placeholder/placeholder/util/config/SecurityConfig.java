package com.placeholder.placeholder.util.config;

import com.placeholder.placeholder.util.CustomAccessDeniedHandler;
import com.placeholder.placeholder.util.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.util.Set;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // Oauth2 Auth Server Config
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfigurer authServer = OAuth2AuthorizationServerConfigurer.authorizationServer();


        http
                .cors(Customizer.withDefaults())
                .securityMatcher(authServer.getEndpointsMatcher())
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .with(authServer, configurer -> {
                    configurer
                            .oidc(Customizer.withDefaults()); // Enable OpenID Connect 1.0
                })
                .exceptionHandling(exceptions -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )
                );

        return http.build(); // Default CORS
    }


    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE+1) // Order = 1
    public SecurityFilterChain formLoginSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/login", "/register",  "/login_process", "/css/**", "/js/**", "/images/**")
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .formLogin(form -> form
                        .loginPage("/login")                   // GET /login -> show your login form
                        .loginProcessingUrl("/login_process") // POST /login_process -> processed by Spring Security
                        .failureUrl("/login?error=true")            // on failure, redirect to /login?error
                        .permitAll()
                )
                .logout(logout -> logout.
                        logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                );

        return http.build();
    }



    // For resource server
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE+2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   Set<String> allowedEndpoints,
                                                   Set<String> allowedStaticEndpoints,
                                                   CustomAccessDeniedHandler accessDeniedHandler,
                                                   CustomAuthenticationEntryPoint authenticationEntryPoint) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    allowedEndpoints.forEach(endpoint ->
                            auth.requestMatchers(endpoint).permitAll()
                    );
                    allowedStaticEndpoints.forEach(staticEndpoint ->
                            auth.requestMatchers(staticEndpoint).permitAll()
                    );
                    auth.anyRequest().authenticated();
                })
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .oauth2ResourceServer((oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())))
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                                                       PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}