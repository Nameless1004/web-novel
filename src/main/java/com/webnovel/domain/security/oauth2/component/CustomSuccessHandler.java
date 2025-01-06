package com.webnovel.domain.security.oauth2.component;

import com.webnovel.domain.security.jwt.TokenType;
import com.webnovel.domain.security.oauth2.dto.CustomOAuth2User;
import com.webnovel.domain.security.jwt.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    @Value("${FRONTEND_URL}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        //OAuth2User
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String username = customOAuth2User.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority authority = iterator.next();
        String role = authority.getAuthority();
        String token = jwtUtil.generateJwt(customOAuth2User.getId(), username, role, TokenType.ACCESS);
        String refresh = jwtUtil.generateJwt(customOAuth2User.getId(), username, role, TokenType.REFRESH);
        response.sendRedirect(frontendUrl + "/login/oauth2.0/?authorization=" + token +
                "&userId="+customOAuth2User.getId() +"&refresh="+refresh);
    }
}