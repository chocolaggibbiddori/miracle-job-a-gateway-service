package com.miracle.memberservice.jwt;

import com.miracle.memberservice.jwt.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtProvider {

    private final Key key;
    private final long accessTokenValidityInSeconds;
    private final long refreshTokenValidityInSeconds;

    public JwtProvider(String secretKey, long accessTokenValidityInSeconds, long refreshTokenValidityInSeconds) {
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
        this.accessTokenValidityInSeconds = accessTokenValidityInSeconds;
        this.refreshTokenValidityInSeconds = refreshTokenValidityInSeconds;
    }

    public Map<String, String> createTokens(String subject, Map<String, Object> claims) {
        String accessToken = createAccessToken(subject, claims);
        String refreshToken = createRefreshToken(subject, claims);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }

    public String createAccessToken(String subject, Map<String, Object> claims) {
        LocalDateTime localDateTime = LocalDateTime.now();
        Date now = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date expirationDate = Date.from(localDateTime.plusSeconds(accessTokenValidityInSeconds).atZone(ZoneId.systemDefault()).toInstant());

        JwtBuilder jwtBuilder = baseJwtBuilder(subject, claims);
        return jwtBuilder
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .compact();
    }

    public String createRefreshToken(String subject, Map<String, Object> claims) {
        LocalDateTime localDateTime = LocalDateTime.now();
        Date now = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date expirationDate = Date.from(localDateTime.plusSeconds(refreshTokenValidityInSeconds).atZone(ZoneId.systemDefault()).toInstant());

        JwtBuilder jwtBuilder = baseJwtBuilder(subject, claims);
        return jwtBuilder
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .compact();
    }

    private JwtBuilder baseJwtBuilder(String subject, Map<String, Object> claims) {
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(subject)
                .signWith(key, SignatureAlgorithm.HS512)
                .addClaims(claims);
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Date expirationDate = claims.getExpiration();
            Date now = new Date();

            return now.before(expirationDate);
        } catch (Exception e) {
            return false;
        }
    }

    public String refreshAccessToken(String refreshToken, String subject, Map<String, Object> claims) {
        if (validateToken(refreshToken)) {
            return createAccessToken(subject, claims);
        }

        throw new InvalidTokenException();
    }

    private String extractSubject(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }
}
