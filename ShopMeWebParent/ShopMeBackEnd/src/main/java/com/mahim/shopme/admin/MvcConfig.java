package com.mahim.shopme.admin;

import com.mahim.shopme.admin.paging.PagingAndSortingArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.mahim.shopme.common.util.StaticPathUtils.*;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        exposeDirectory(USER_UPLOAD_DIR, registry);
        exposeDirectory(CATEGORY_UPLOAD_DIR, registry);
        exposeDirectory(BRAND_UPLOAD_DIR, registry);
        exposeDirectory(PRODUCT_UPLOAD_DIR, registry);
        exposeDirectory(SITE_LOGO_DIR, registry);
    }

    private void exposeDirectory(String pathPattern, ResourceHandlerRegistry registry) {
        Path path = Paths.get(pathPattern);
        String absolutePath = path.toFile().getAbsolutePath();

        registry.addResourceHandler("/" + pathPattern + "/**")
                .addResourceLocations("file:///" + absolutePath + "/");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new PagingAndSortingArgumentResolver());
    }
}
