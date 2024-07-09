package com.iot.system.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.iot.system.exception.ExpiredJwtException;
import com.iot.system.exception.InvalidJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.secret}")
    private String secret;

    public String extractUsername(String token) throws ExpiredJwtException, InvalidJwtException {
        return extractClaim(token, DecodedJWT::getSubject);
    }

    public <T> T extractClaim(String token, Function<DecodedJWT, T> claimsResolver) throws ExpiredJwtException, InvalidJwtException {
        final DecodedJWT jwt = decodeJWT(token);
        return claimsResolver.apply(jwt);
    }

    public String generateToken(UserDetails userDetails) {
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .sign(Algorithm.HMAC256(secret));
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) throws ExpiredJwtException, InvalidJwtException {
        return extractClaim(token, DecodedJWT::getExpiresAt).before(new Date());
    }

    private DecodedJWT decodeJWT(String token) throws ExpiredJwtException, InvalidJwtException {
        try {
            return JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
        } catch (com.auth0.jwt.exceptions.TokenExpiredException e) {
            throw new ExpiredJwtException("Token has expired");
        } catch (JWTVerificationException e) {
            throw new InvalidJwtException("Invalid token");
        }
    }
}
