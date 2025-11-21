package com.example.social_media.shared_libs.configuration;

import com.example.social_media.shared_libs.utils.RestClientUtility;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class SharedLibsAutoConfiguration {

    @Bean
    public RestClientUtility restClientUtility() {
        return new RestClientUtility();
    }
}
