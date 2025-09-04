package com.cloudtone31.global.config;


import com.cloudtone31.auth.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationSuccessHandler oAuth2SuccessHandler;
    private final AuthenticationFailureHandler oAuth2FailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/health", "/actuator/**", "/error",
                                "/swagger-ui/**", "/v3/api-docs/**",
                                "/oauth2/**", "/login/**",
                                "/v1/auth/kakao/url", "/v1/auth/kakao/callback"
                        ).permitAll()
                        .requestMatchers("/users/me","/v1/auth/kakao/logout").authenticated()
                        .requestMatchers("/v1/auth/kakao/nickname").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/users/delete").authenticated()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .loginPage("/oauth2/authorization/kakao")
                        .userInfoEndpoint(user -> user.userService(customOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler)
                )
                .logout(logout -> logout
                        .logoutUrl("/v1/auth/kakao/logout") // 문서 규격에 맞춤
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        // Postman/백엔드 통신에선 리다이렉트 대신 204로 끝내면 편합니다.
                        .logoutSuccessHandler((req, res, auth) -> res.setStatus(204))
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint((req, res, e) -> {
                    res.setStatus(401);
                    res.setContentType("application/json;charset=UTF-8");
                    // {"success": false, "message": "인증이 필요합니다."}
                    res.getWriter().write("{\"success\":false,\"message\":\"인증이 필요합니다.\"}");
                }));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();
        // application.yml 의 cors.allowed-origins 와 일치
        c.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173"));
        c.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        c.setAllowedHeaders(List.of("*"));
        c.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", c);
        return src;
    }
}