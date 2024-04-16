package com.traverse.apigateway.configs;


import com.traverse.apigateway.filter.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import java.util.Collections;

@Configuration
public class RouteConfig {

    @Autowired
    private AuthenticationFilter authFilter;

    @Value("${auth-service.uri}")
    private String authServiceURI;

    @Value("${main-service.uri}")
    private String mainServiceURI;

    @Value("${storage-service.uri}")
    private String storageServiceURI;

    @Value("${traverse-ui.uri}")
    private String frontEndURI;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r
                        .path("/auth/**")
                        .filters(f -> f.rewritePath("/auth/(?<segment>.*)", "/auth/${segment}"))
                        .uri("http://127.0.0.1:8050"))

                .route("auth-service-user", r -> r
                        .path("/user/**")
                        .filters(f -> f.filter(authFilter).rewritePath("/user/(?<segment>.*)", "/user/${segment}"))
                        .uri("http://127.0.0.1:8050"))

                .route("main-service", r -> r
                        .path("/main-service/**")
                        .filters(f -> f.filter(authFilter).rewritePath("/main-service/(?<segment>.*)", "/${segment}"))
                        .uri("http://127.0.0.1:8000"))

                .route("storage-service", r -> r
                        .path("/storage-service/**")
                        .filters(f -> f.filter(authFilter).rewritePath("/storage-service/(?<segment>.*)", "/${segment}"))
                        .uri("http://127.0.0.1:8080"))

                .route("traverse-ui", r -> r
                        .path("/**")
                        .filters(f -> f.rewritePath("/(?<segment>.*)", "/${segment}"))
                        .uri("http://127.0.0.1:3000"))

                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration apiCorsConfiguration = new CorsConfiguration();
        apiCorsConfiguration.setAllowCredentials(true);
        apiCorsConfiguration.setAllowedOriginPatterns(Collections.singletonList("*"));
        apiCorsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
        apiCorsConfiguration.setAllowedMethods(Collections.singletonList("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", apiCorsConfiguration);
        return source;
    }


}
