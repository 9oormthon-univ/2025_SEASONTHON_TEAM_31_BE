package com.cloudtone31.global.config;

import com.cloudtone31.auth.service.CustomOAuth2UserService;
import com.cloudtone31.auth.service.JwtService;
import com.cloudtone31.global.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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
    private final ClientRegistrationRepository clientRegistrationRepository;

    private final JwtService jwtService; // 주입 받아야 함

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter(jwtService);
    }


    /**
     * Kakao 권한요청 시 항상 로그인 화면을 띄우기 위한 커스터마이저 (자동승인/자동로그인 방지)
     */
    @Bean
    public OAuth2AuthorizationRequestResolver kakaoAuthorizationRequestResolver() {
        DefaultOAuth2AuthorizationRequestResolver delegate =
                new DefaultOAuth2AuthorizationRequestResolver(
                        clientRegistrationRepository,
                        "/oauth2/authorization" // 기본 매핑
                );

        delegate.setAuthorizationRequestCustomizer(customizer ->
                customizer.additionalParameters(params -> {
                    // 항상 로그인 화면 강제. (필요시 "consent" 또는 "login consent" 등으로 조정)
                    params.put("prompt", "login");
                })
        );
        return delegate;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           OAuth2AuthorizationRequestResolver kakaoAuthResolver, JwtAuthFilter jwtAuthFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionFixation(sf -> sf.migrateSession())
                )
                .authorizeHttpRequests(auth -> auth
                        // 1) 로그인 시작 URL은 '로그인 안 된 사용자만'
                        .requestMatchers("/oauth2/authorization/**").anonymous()

                        // 2) 나머지 공개 엔드포인트
                        .requestMatchers(
                                "/", "/error",
                                "/health", "/actuator/**",
                                "/index.html",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs", "/v3/api-docs/**",
                                "/oauth2/**", "/login/**",
                                "/v1/auth/kakao/url", "/v1/auth/kakao/callback",
                                "/v1/auth/kakao/mobile-url",
                                "/v1/auth/token/exchange",
                                "/v1/auth/token/exchange-code",
                                "/v1/auth/token/refresh"
                        ).permitAll()

                        // 3) 인증 필요
                        .requestMatchers("/users/me", "/v1/auth/kakao/logout").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/users/delete").authenticated()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .loginPage("/oauth2/authorization/kakao")
                        .authorizationEndpoint(ep -> ep
                                .authorizationRequestResolver(kakaoAuthResolver)
                        )
                        .userInfoEndpoint(user -> user.userService(customOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler)
                )
                .logout(logout -> logout
                        .logoutUrl("/v1/auth/kakao/logout")
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler((req, res, auth) -> res.setStatus(204)) // 리다이렉트 대신 204
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint((req, res, e) -> {
                    res.setStatus(401);
                    res.setContentType("application/json;charset=UTF-8");
                    res.getWriter().write("{\"success\":false,\"message\":\"인증이 필요합니다.\"}");
                }))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);;

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // 필요시 application.yml의 cors.allowed-origins를 읽어오도록 변경 가능
        CorsConfiguration c = new CorsConfiguration();
        c.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173"));
        c.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        c.setAllowedHeaders(List.of("*"));
        c.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", c);
        return src;
    }
}