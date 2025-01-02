package com.webnovel.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private SecretKey secretKey;
    public static final long ACCESS_LIFE_TIME_MS = 60 * 60 * 1000;
    public static final long REFRESH_LIFE_TIME_MS = 24 * 60 * 60 * 1000;
    public static final String TOKEN_PREFIX = "Bearer ";

    public JwtUtil(@Value("${jwt.secret.key}") String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getId(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("id", String.class);
    }

    public String getUsername(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public String getType(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("type", String.class);
    }

    public String generateJwt(String id, String username, String role, TokenType tokenType) {
        return Jwts.builder()
                .claim("id", id)
                .claim("username", username)
                .claim("role", role)
                .claim("type", tokenType.toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenType.getLIFETIME_MS()))
                .signWith(secretKey)
                .compact();
    }

    public void addRefreshTokenToCookie(String token, HttpServletResponse response) {
        response.addCookie(new Cookie("refresh_token", token) {{
            setHttpOnly(true);  // 자바스크립트에서 접근할 수 없게
          //  setSecure(true);     // HTTPS에서만 전송
            setPath("/");
            setMaxAge((int)TokenType.REFRESH.getLIFETIME_MS() / 1000);  // 예: 7일 동안 유효
        }});
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);  // 만료된 토큰이라면 false
        } catch (ExpiredJwtException e) {
            return false;
        } catch (SignatureException e) {
            return false;
        } catch (MalformedJwtException e) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration().before(new java.util.Date());
    }


    public void addAccessTokenToHeader(String accessToken, HttpServletResponse response) {
        response.addHeader("Authorization", accessToken);
    }
}
