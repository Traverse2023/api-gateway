package com.traverse.apigateway.configs;

import java.util.function.Predicate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class RouterValidator {

    public static final List<String> openEndpoints = List.of(
            "auth/register",
            "auth/login"
    );

    public Predicate<ServerHttpRequest> isSecured = request ->
            openEndpoints.stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));
}
