package com.alephzero.alephzero.infrastructure.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Serves the React single-page application from the backend for client-side routes.
 * <p>
 * The API lives under {@code /api/**} and is handled by the REST controllers, which
 * take precedence (they are matched before these mappings). Everything else that
 * looks like an application route (no file extension) is forwarded to
 * {@code /index.html}, so React Router can resolve it on the client.
 * <p>
 * Paths containing a dot (static assets such as {@code /assets/index-*.js},
 * {@code /icon.png}, {@code /favicon.ico}...) are intentionally <b>not</b> matched
 * here, so they fall through to Spring Boot's static resource handler.
 */
@Controller
public class SpaRoutingController {

    /**
     * Forwards client-side routes to the SPA entry point.
     * <p>
     * Covers the root and nested routes up to three segments (e.g. {@code /login},
     * {@code /history/42}, {@code /settings/theme/dark}), always excluding paths
     * that contain a file extension.
     *
     * @return a forward to the React entry point
     */
    @GetMapping({
            "/",
            "/{p1:[^\\.]*}",
            "/{p1:[^\\.]*}/{p2:[^\\.]*}",
            "/{p1:[^\\.]*}/{p2:[^\\.]*}/{p3:[^\\.]*}"
    })
    public String forwardToSpa() {
        return "forward:/index.html";
    }
}
