package com.traverse.apigateway.jwt;


import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.function.Function;


/**
 * Util class which contains all JWT related methods used for verifying token, creating token,
 * parsing token, etc... Algorithm HS512 with a private key passed in through
 * application.yml is used to sign and verify token.
 * */
@Service
public class JwtUtil {

    @Value("${jwt.key}")
    private String key;

    /**
     * Performs validation on tokens by attempting to parse with given key. Will throw a
     * {@link java.security.SignatureException} if the token is invalid or tampered with.
     * Throws a {@link ExpiredJwtException} if the token is expired and requires refreshing.
     * If validation is successful,  the userId as {@link String} is returned.
     *<p>
     * @param token a token to be validated
     * @return the userId payload contained within a token
     * */
    public String validateToken(String token) throws SignatureException, ExpiredJwtException {
        JwtParser parser = Jwts.parserBuilder().setSigningKey(getKey()).build();
        parser.parseClaimsJws(token);
        return getUserId(token);
    }

    /**
     * Helper method base64 decodes the key {@link String} and defines the algorithm to be used to
     * create a {@link SecretKeySpec} to be used to encrypt and decrypt the jwt token payload.
     *
     * @return a HS512 key used to sign token
     * */
    public Key getKey() {
        return new SecretKeySpec(Base64.getDecoder().decode(key), SignatureAlgorithm.HS512.getJcaName());
    }

    /**
     * Gets encrypted userId and resolves as a {@link String} from a supplied token.
     *
     * @param token a token to extract a user id from
     * @return a user id
     * */
    public String getUserId(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Extracts a single {@link Claims} from token based on a given parameter function.
     *
     * @param token a token to be parsed
     * @param claimsResolver a function defining the claim to retrieve
     * @return the claim desired parsed from the token
     * */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Get all claims parsed from a given token. Uses the specific Key to decrypt the token payload.
     *
     * @param token the token to be parsed
     * @return the claims decrypted from the token
     * */
    private Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token).getBody();
    }

}
