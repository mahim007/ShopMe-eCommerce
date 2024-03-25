package com.mahim.shopme;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.mahim.shopme.utils.StaticPathUtils.*;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        exposeDirectory(CATEGORY_UPLOAD_DIR, registry);
        exposeDirectory(BRAND_UPLOAD_DIR, registry);
        exposeDirectory(PRODUCT_UPLOAD_DIR, registry);
    }

    private void exposeDirectory(String pathPattern, ResourceHandlerRegistry registry) {
        Path path = Paths.get(pathPattern);
        String absolutePath = path.toFile().getAbsolutePath();

        registry.addResourceHandler("/" + pathPattern + "/**")
                .addResourceLocations("file:///" + absolutePath + "/");
    }
}
