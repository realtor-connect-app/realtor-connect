package com.makurohashami.realtorconnect.cdn.config;

import com.cloudinary.Cloudinary;
import com.makurohashami.realtorconnect.cdn.condition.CloudinaryCdnEnabled;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "cloudinary")
@Conditional(CloudinaryCdnEnabled.class)
public class CloudinaryConfiguration {

    private String cloudinaryUrl;

    @Bean
    @Conditional(CloudinaryCdnEnabled.class)
    public Cloudinary cloudinary() {
        return new Cloudinary(getCloudinaryUrl());
    }

}
