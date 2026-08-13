package com.socialnetwork.common.security;

import com.socialnetwork.module.user.entity.User;
import com.socialnetwork.module.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String ACCESS_TOKEN_COOKIE = "access_token";

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            String token = getTokenFromCookie(request);

            if (token != null
                    && jwtTokenProvider.validateToken(token)
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                UUID userId = jwtTokenProvider.getUserIdFromToken(token);

                User user = userRepository.findById(userId)
                        .orElse(null);

                if (user != null) {
                    // 1. Wrap entity 'User' vào 'CustomUserDetails'
                    CustomUserDetails userDetails = new CustomUserDetails(user);

                    // 2. Chỉ lập authentication khi tài khoản ở trạng thái enabled
                    if (userDetails.isEnabled()) {
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails, // <-- Truyền userDetails làm principal
                                        null,
                                        userDetails.getAuthorities() // <-- Lấy authorities trực tiếp từ userDetails
                                );

                        SecurityContextHolder
                                .getContext()
                                .setAuthentication(authentication);
                    }
                }
            }

        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromCookie(
            HttpServletRequest request
    ) {

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (ACCESS_TOKEN_COOKIE.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }
}