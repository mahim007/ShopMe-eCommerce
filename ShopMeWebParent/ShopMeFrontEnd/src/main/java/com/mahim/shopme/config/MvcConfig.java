package com.mahim.shopme.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        exposeDirectory(CATEGORY_UPLOAD_DIR, registry);
//        exposeDirectory(BRAND_UPLOAD_DIR, registry);
//        exposeDirectory(PRODUCT_UPLOAD_DIR, registry);
//        exposeDirectory(SITE_LOGO_DIR, registry);
//    }
//
//    private void exposeDirectory(String pathPattern, ResourceHandlerRegistry registry) {
//        Path path = Paths.get(pathPattern);
//        String absolutePath = path.toFile().getAbsolutePath();
//
//        registry.addResourceHandler("/" + pathPattern + "/**")
//                .addResourceLocations("file:///" + absolutePath + "/");
//    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
