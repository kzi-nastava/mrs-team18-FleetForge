package com.team18.FleetForge.util;

import com.team18.FleetForge.model.users.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenUtils {

    @Value("FleetForgeApp")
    private String APP_NAME;

    private final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    @Value("1800000") // 30 minutes in milliseconds
    private int EXPIRES_IN;

    @Value("Authorization")
    private String AUTH_HEADER;

    public String generateToken(User user) {
        return Jwts.builder()
                .setIssuer(APP_NAME)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRES_IN))
                .claim("roles", user.getRole())
                .signWith(SECRET_KEY)
                .compact();
    }

    // Extracts JWT from the HTTP Authorization header.
    public String getToken(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTH_HEADER);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    public String getUsernameFromToken(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Boolean validateToken(String token, String userDetailsUsername) {
        final String username = getUsernameFromToken(token);
        return (username != null &&
                username.equals(userDetailsUsername) &&
                !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        Date expiration = getAllClaimsFromToken(token).getExpiration();
        return expiration.before(new Date());
    }
}