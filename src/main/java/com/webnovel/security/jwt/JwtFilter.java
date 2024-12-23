package com.webnovel.security.jwt;

import com.webnovel.security.oauth2.dto.CustomOAuth2User;
import com.webnovel.security.oauth2.dto.UserDto;
import io.jsonwebtoken.lang.Strings;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        String requestURI = request.getRequestURI();

        log.info(":::Request URI::: [ {} ]", requestURI);

        if(requestURI.startsWith("/api/auth/login") || requestURI.startsWith("/api/auth/signup")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (header == null || !header.startsWith("Bearer ")) {
            // 회원가입 안돼있을 때 넘겨줌
            filterChain.doFilter(request, response);
            return;
        }

        // "Bearer " 제거
        String token = header.substring(7);

        if(jwtUtil.isTokenExpired(token)) {
            log.warn("Token is expired");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is expired");
            return;
        }

        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        if(Strings.hasText(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
            AuthUser authUser = new AuthUser(username, role);
            JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(authUser);
            jwtAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(jwtAuthenticationToken);
        }

        filterChain.doFilter(request, response);

//        UserDto userDto = new UserDto(username, role);
//
//        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDto);
//
//        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
//        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    /**
     * 에러 응답 처리
     */
    private void handleError(HttpServletResponse response, int status, String message, Exception e) throws IOException {
        log.error(message, e);
        response.setStatus(status);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(String.format("{\"message\": \"%s\"}", message));
        response.getWriter().flush();
        SecurityContextHolder.clearContext(); // 인증 정보 초기화
    }

}
