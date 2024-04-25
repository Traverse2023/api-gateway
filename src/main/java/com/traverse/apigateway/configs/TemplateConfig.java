package com.traverse.apigateway.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TemplateConfig {


    /**
     * Exposes a {@link RestTemplate} bean used to make requests within
     * filters and other classes to helper services or apis.
     * @return a rest template used to make http requests
     * */
    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

}
