package com.traverse.apigateway.configs;


import com.traverse.apigateway.filter.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration to set routes, filters, and mutations etc...
 * Set other configurations related to API Gateway such as CORS.
 * Endpoints are set as env variables and defined in application.yaml
 * */
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

    /**
     * Expose a bean that defines routing configurations.
     * Can apply filters to perform validation or mutation on request before forwarding to specified URIs.
     * An {@link AuthenticationFilter} is set for endpoints that require the user be authenticated prior.
     *
     * @param builder a bean of {@link RouteLocatorBuilder}.
     * @return A {@link RouteLocator} which defines route configurations.
     * */
    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("agora", r -> r
                        .path("/agora/**")
                        .filters(f -> f.filter(authFilter).rewritePath("/agora/(?<segment>.*)", "/${segment}")
                                .setResponseHeader("Access-Control-Allow-Origin", "*")
                        )
                        .uri(mainServiceURI))

                .route("auth-service", r -> r
                        .path("/auth/**")
                        .filters(f -> f.rewritePath("/auth/(?<segment>.*)", "/auth/${segment}")
                                .setResponseHeader("Access-Control-Allow-Origin", "*")
                                )
                        .uri(authServiceURI))

                .route("auth-service-user", r -> r
                        .path("/user/**")
                        .filters(f -> f.filter(authFilter).rewritePath("/user/(?<segment>.*)", "/user/${segment}")
                                .setResponseHeader("Access-Control-Allow-Origin", "*"))
                        .uri(authServiceURI))

                .route("main-service", r -> r
                        .path("/main-service/**")
                        .filters(f -> f.filter(authFilter).rewritePath("/main-service/(?<segment>.*)", "/api/${segment}")
                                .setResponseHeader("Access-Control-Allow-Origin", "*"))
                        .uri(mainServiceURI))

                .route("storage-service", r -> r
                        .path("/storage-service/**")
                        .filters(f -> f.filter(authFilter).rewritePath("/storage-service/(?<segment>.*)", "/api/v1/${segment}")
                                .setResponseHeader("Access-Control-Allow-Origin", "*"))
                        .uri(storageServiceURI))

                .route("sockets", r -> r
                        .path("/socket.io/**")
                        .filters(f -> f.filter(authFilter).rewritePath("/socket.io/(?<segment>.*)", "/socket.io/${segment}")
                                .setResponseHeader("Access-Control-Allow-Origin", "*"))
                        .uri(mainServiceURI))

                .route("health-check", r -> r
                        .path("/actuator/**")
                        .filters(f ->  f.setStatus(200))
                        .uri("no://op"))

//                .route("traverse-ui", r -> r
//                        .path("/profile/**")
//                        .filters(f ->  f.rewritePath("/profile/(?<segment>.*)", "/"))
//                        .uri(frontEndURI))

                .route("traverse-ui", r -> r
                        .path("/**")
                        //.filters(f ->  f.rewritePath("/(?<segment>.*)", "/"))
                        .uri(frontEndURI))

                .build();
    }

}
