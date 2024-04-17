package com.traverse.apigateway.jwt;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

import java.util.function.Function;

@Service
public class JwtUtil {

    @Value("${jwt.key}")
    private String key;

    public String validateToken(String token) throws SignatureException, ExpiredJwtException {
        JwtParser parser = Jwts.parserBuilder().setSigningKey(getKey()).build();
        parser.parseClaimsJws(token);
        return getUserId(token);
    }

    public Key getKey() {
        return new SecretKeySpec(Base64.getDecoder().decode(key), SignatureAlgorithm.HS512.getJcaName());
    }

    public String getUserId(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isExpired (String token) {
        Claims claims = getClaims(token);
        return !(claims.getExpiration().after(new Date(System.currentTimeMillis())));
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token).getBody();
    }
}
