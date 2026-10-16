package com.alephzero.alephzero.infrastructure.security;

import com.alephzero.alephzero.infrastructure.config.JwtProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

/**
 * Creates, reads and clears the HttpOnly cookie that carries the refresh token.
 * <p>
 * The cookie is scoped to {@code /api/v1/auth} so it is only sent to the
 * authentication endpoints, and marked HttpOnly so it is never accessible from
 * JavaScript.
 */
@Component
@RequiredArgsConstructor
public class AuthCookieService {

    /** The refresh cookie is only sent to these endpoints. */
    private static final String COOKIE_PATH = "/api/v1/auth";

    private final JwtProperties jwtProperties;

    /**
     * Writes the refresh token cookie to the response.
     *
     * @param response   the current response
     * @param token      the raw refresh token
     * @param persistent if {@code true} the cookie survives browser restarts
     *                   ("remember me"); otherwise it is a session cookie
     */
    public void setRefreshToken(HttpServletResponse response, String token, boolean persistent) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie
                .from(jwtProperties.getCookieName(), token)
                .httpOnly(true)
                .secure(jwtProperties.isCookieSecure())
                .sameSite("Lax")
                .path(COOKIE_PATH);

        if (persistent) {
            builder.maxAge(jwtProperties.getRefreshTtl());
        }

        response.addHeader(HttpHeaders.SET_COOKIE, builder.build().toString());
    }

    /**
     * Reads the refresh token from the request cookies.
     *
     * @param request the current request
     * @return the raw refresh token, or {@code null} if not present
     */
    public String readRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (jwtProperties.getCookieName().equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * Clears the refresh token cookie.
     *
     * @param response the current response
     */
    public void clearRefreshToken(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie
                .from(jwtProperties.getCookieName(), "")
                .httpOnly(true)
                .secure(jwtProperties.isCookieSecure())
                .sameSite("Lax")
                .path(COOKIE_PATH)
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
