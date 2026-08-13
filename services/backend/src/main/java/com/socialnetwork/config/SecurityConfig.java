package com.socialnetwork.config;

import com.socialnetwork.common.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // Import HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(
                                (request, response, authException) -> {
                                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                }
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // 1. PUBLIC ENDPOINTS
                        .requestMatchers(
                                "/api/health",
                                "/api/auth/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // 2. PUBLIC API ĐĂNG KÝ USER (Bổ sung dòng này!)
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()

                        // 3. ADMIN APIS
                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        // 4. MODERATOR APIS
                        .requestMatchers("/api/moderator/**")
                        .hasAnyRole("ADMIN", "MODERATOR")

                        // 5. CÁC API KHÁC THUỘC USER (GET /profile, PUT /profile,...) YÊU CẦU LOGIN
                        .requestMatchers("/api/users/**")
                        .hasAnyRole(
                                "USER",
                                "MODERATOR",
                                "ADMIN"
                        )

                        .anyRequest()
                        .authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}