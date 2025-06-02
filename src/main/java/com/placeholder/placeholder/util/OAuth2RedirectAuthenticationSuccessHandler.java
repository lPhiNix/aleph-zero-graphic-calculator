package com.placeholder.placeholder.util;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@RequiredArgsConstructor
public class OAuth2RedirectAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final RegisteredClientRepository registeredClientRepository;
    private final RequestCache requestCache = new HttpSessionRequestCache();
    private final String clientId;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        SavedRequest savedRequest = requestCache.getRequest(request, response);

        if (savedRequest != null) {
            String targetUrl = savedRequest.getRedirectUrl();
            response.sendRedirect(targetUrl);
            return;
        }

        RegisteredClient client = registeredClientRepository.findByClientId(clientId);

        if (client == null) {
            // fallback,
            response.sendRedirect("/login?error=true");
            return;
        }

        // Usa el primer redirectUri (puede haber varios)
        String redirectUri = client.getRedirectUris().stream().findFirst().orElse("");
        String scopes = String.join(" ", client.getScopes());

        String authorizeUrl = UriComponentsBuilder.fromPath("/oauth2/authorize")
                .queryParam("client_id", client.getClientId())
                .queryParam("response_type", "code")
                .queryParam("scope", scopes)
                .queryParam("redirect_uri", redirectUri)
                .build().toUriString();

        response.sendRedirect(authorizeUrl);
    }
}

