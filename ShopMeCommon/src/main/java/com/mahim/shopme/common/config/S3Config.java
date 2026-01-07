package com.mahim.shopme.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter @Setter
public class S3Config {
    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.region}")
    private String region;

    public String getBaseS3Url() {
        return "https://" + bucket + ".s3." + region + ".amazonaws.com";
    }
}
