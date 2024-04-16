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


@Slf4j
@Component
public class AuthenticationFilter implements GatewayFilter {

    @Autowired
    private RouterValidator routerValidator;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if(routerValidator.isSecured.test(request)) {
            if(!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                log.info("No authorization header in request.");
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }
            String token = getToken(request);
            String userId = jwtUtil.validateToken(token);
            if (token.isEmpty() || userId.isEmpty()) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }
            log.info("Validated and retrieved user node id from token: {}", userId);
            ServerHttpRequest newRequest =  request.mutate().header("x-user", userId).build();
            return chain.filter(exchange.mutate().request(newRequest).build());
        }
        return chain.filter(exchange);
    }

    private String getToken(ServerHttpRequest request) {
        String authHeader = Objects.requireNonNull(request.getHeaders().get(HttpHeaders.AUTHORIZATION)).get(0);
        if (authHeader !=null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return "";
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

}
