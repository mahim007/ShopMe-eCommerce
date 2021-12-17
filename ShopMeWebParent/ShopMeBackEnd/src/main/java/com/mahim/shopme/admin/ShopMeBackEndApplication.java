package com.mahim.shopme.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({"com.mahim.shopme.common.entity", "com.mahim.shopme.admin.user"})
public class ShopMeBackEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopMeBackEndApplication.class, args);
    }

}
