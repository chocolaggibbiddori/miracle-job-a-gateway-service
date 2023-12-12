package com.miracle.memberservice.jwt;

import com.miracle.memberservice.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class JwtProvider {

    private final Key key;
    private final long accessTokenValidityInSeconds;
    private final long refreshTokenValidityInSeconds;

    public JwtProvider(String secretKey, long accessTokenValidityInSeconds, long refreshTokenValidityInSeconds) {
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
        this.accessTokenValidityInSeconds = accessTokenValidityInSeconds;
        this.refreshTokenValidityInSeconds = refreshTokenValidityInSeconds;
    }

    public Map<String, String> createTokens(Long id, String subject) {
        String accessToken = createAccessToken(id, subject);
        String refreshToken = createRefreshToken(id, subject);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }

    public String createAccessToken(Long id, String subject) {
        LocalDateTime localDateTime = LocalDateTime.now();
        Date now = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date expirationDate = Date.from(localDateTime.plusSeconds(accessTokenValidityInSeconds).atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .claim("id", id)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String createRefreshToken(Long id, String subject) {
        LocalDateTime localDateTime = LocalDateTime.now();
        Date now = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date expirationDate = Date.from(localDateTime.plusSeconds(refreshTokenValidityInSeconds).atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .claim("id", id)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean validateToken(Long id, String subject, String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Long parsedId = claims.get("id", Long.class);
            String parsedSub = claims.getSubject();
            Date expirationDate = claims.getExpiration();
            Date now = new Date();

            return Objects.equals(parsedId, id) && Objects.equals(parsedSub, subject) && now.before(expirationDate);
        } catch (Exception e) {
            return false;
        }
    }

    public String refreshAccessToken(Long id, String subject, String refreshToken) {
        if (validateToken(id, subject, refreshToken)) {
            return createRefreshToken(id, subject);
        }

        throw new InvalidTokenException();
    }

    private String extractSubject(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }
}
