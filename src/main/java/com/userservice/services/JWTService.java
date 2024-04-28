package com.userservice.services;

import com.userservice.models.User;
import io.jsonwebtoken.Jws;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class JWTService {
    private final String JWT_SERVICE_SECRET = "bXlyZWFsbHlsb25nc2VjcmV0d2l0aHlvdQ==";
    private final long ONE_WEEK = 1000 * 60 * 60 * 24 * 7;
    private final Key key = Keys.hmacShaKeyFor(JWT_SERVICE_SECRET.getBytes(StandardCharsets.UTF_8));
    private final JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(key).build();

    public String createJWT(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ONE_WEEK))
                .signWith(key)
                .compact();
    }

    public String decodeJWT(String jwt) {
        Claims claims = jwtParser.parseClaimsJws(jwt).getBody();

        return claims.getSubject();
    }
}
