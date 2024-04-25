package com.traverse.apigateway.filter;

import com.traverse.apigateway.configs.RouterValidator;
import com.traverse.apigateway.jwt.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.Objects;


/**
 * An implementation of {@link GatewayFilter} which may form a part of a longer filter chain.
 * This filter is used to qualify requests to secure endpoints by verifying the integrity of a jwt token.
 * Once the user is verified, the userId is inserted as a header and forwarded to the endpoint.
 * */
@Slf4j
@Component
public class AuthenticationFilter implements GatewayFilter {

    @Autowired
    private RouterValidator routerValidator;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Overridden method which performs the filtering action on the incoming request.
     *
     * @param exchange a {@link ServerWebExchange} containing the request, response and other related attributes
     * which can be accessed and modified as needed.
     * @param chain a {@link GatewayFilterChain}. A chain of assigned {@link GatewayFilter}s. Filters can
     * delegate to the following filters in the chain.
     * @return {@link Mono<Void>} a type of publisher that returns a single value and signals completion.
     * @see <a href="https://www.baeldung.com/java-reactor-flux-vs-mono">Java Reactor-Flux v Mono</a>
     * */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if (routerValidator.isSecured.test(request)) {
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                log.info("Error: No authorization header in request.");
                return onError(exchange.getResponse(), HttpStatus.UNAUTHORIZED);
            }
            try {
                String token = getToken(request);
                String userId = jwtUtil.validateToken(token);
                log.info("Validated and retrieved user node id from token: {}", userId);
                ServerHttpRequest newRequest =  request.mutate().header("x-user", userId).build();
                return chain.filter(exchange.mutate().request(newRequest).build());
            } catch (Exception e) {
                log.warn("Error validating authorization token: {}", e.getMessage());

                return onError(exchange.getResponse(), HttpStatus.UNAUTHORIZED);
            }
        }
        return chain.filter(exchange);
    }

    /**
     * Helper method extracts a token from a {@link ServerHttpRequest}'s headers. Verifies the headers existence
     * and checks that the header is formatted correctly before returning the token as a {@link String}.
     *
     * @param request a request to authenticate
     * @return a authentication token to validate
     * */
    private String getToken(ServerHttpRequest request) {
        String authHeader = Objects.requireNonNull(request.getHeaders().get(HttpHeaders.AUTHORIZATION)).get(0);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new RuntimeException("Invalid authorization token: token is missing or malformed");
    }

    /**
     * On any validation error this helper function is called to set a {@link HttpStatus} of choice
     * on a given {@link ServerHttpResponse} and returns the newly modified {@link Mono} publisher to be
     * used in responding to the request in question.
     *
     * @param response a response whose status needs to be changed.
     * @param httpStatus the http status which is desired to be returned.
     * @return a mono publisher with a newly set response status
     * */
    private Mono<Void> onError(ServerHttpResponse response, HttpStatus httpStatus) {
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

}
