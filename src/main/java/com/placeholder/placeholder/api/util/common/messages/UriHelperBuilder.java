package com.placeholder.placeholder.api.util.common.messages;


import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * Utility class to build URIs based on the current request context.
 */
public class UriHelperBuilder {

    /**
     * Builds a URI from the current request context and appends the specified path template.
     *
     * @param pathTemplate the path template to append
     * @param uriVariables the variables to expand in the path template
     * @return the constructed URI
     */
    public static URI buildUriFromCurrentRequest(String pathTemplate, Object... uriVariables) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path(pathTemplate)
                .buildAndExpand(uriVariables)
                .toUri();
    }

    /**
     * Builds a URI from the current request context and appends the specified ID.
     *
     * @param id the ID to append to the URI
     * @return the constructed URI
     */
    public static URI buildUriFromCurrentRequest(Object id) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }
}
