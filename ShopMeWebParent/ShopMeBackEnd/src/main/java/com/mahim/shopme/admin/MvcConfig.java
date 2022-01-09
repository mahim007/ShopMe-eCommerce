package com.mahim.shopme.admin;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.mahim.shopme.admin.utils.StaticPathUtils.UPLOAD_DIR;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path userPhotosDir = Paths.get(UPLOAD_DIR);
        String userPhotosPath = userPhotosDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/" + UPLOAD_DIR + "/**")
                .addResourceLocations("file:/" + userPhotosPath + "/");
    }
}
