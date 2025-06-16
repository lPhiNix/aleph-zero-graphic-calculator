package com.alephzero.alephzero.util.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * This handler handles the snapshots folder (history image storage). The class is needed for spring to recognize the
 * folder as 'static resource folder'.
 */
@Configuration
public class ResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/snapshots/**")
                .addResourceLocations("file:" + Paths.get("snapshots").toAbsolutePath() + "/");
    }
}
