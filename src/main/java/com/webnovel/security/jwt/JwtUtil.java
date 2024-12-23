package com.webnovel.security.jwt;

import io.jsonwebtoken.Jwts;
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

    public String getUsername(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public Boolean isTokenExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public String generateJwt(String username, String role, TokenType tokenType) {
        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenType.getLIFETIME_MS()))
                .signWith(secretKey)
                .compact();
    }

    public String prependTokenPrefix(String token) {
        return TOKEN_PREFIX + token;
    }

    public void addRefreshTokenToCookie(String token, HttpServletResponse response) {
        response.addCookie(new Cookie("refresh_token", token) {{
            setHttpOnly(true);  // 자바스크립트에서 접근할 수 없게
          //  setSecure(true);     // HTTPS에서만 전송
            setPath("/");
            setMaxAge((int)TokenType.REFRESH.getLIFETIME_MS() / 1000);  // 예: 7일 동안 유효
        }});
    }

    public void addAccessTokenToHeader(String accessToken, HttpServletResponse response) {
        response.addHeader("Authorization", prependTokenPrefix(accessToken));
    }
}
