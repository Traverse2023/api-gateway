package com.traverse.apigateway.configs;

import java.util.function.Predicate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * Validator class defines open endpoints that do not need authentication filtering.
 * */
@Component
public class RouterValidator {

    public static final List<String> openEndpoints = List.of(
            "auth/register",
            "auth/login"
    );

    /**
     * A {@link Predicate<ServerHttpRequest>} identifying an endpoint as secure or open.
     * Secure endpoints must pass authentication filtering before being forwarded to respective endpoints.
     * */
    public Predicate<ServerHttpRequest> isSecured = request ->
            openEndpoints.stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));
}
