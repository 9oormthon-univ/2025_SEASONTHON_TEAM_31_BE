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

    private final JwtService jwtService;

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter(jwtService);
    }

    /**
     * Kakao 권한요청 시 항상 로그인 화면을 띄우도록 prompt=login 추가
     */
    @Bean
    public OAuth2AuthorizationRequestResolver kakaoAuthorizationRequestResolver() {
        DefaultOAuth2AuthorizationRequestResolver delegate =
                new DefaultOAuth2AuthorizationRequestResolver(
                        clientRegistrationRepository,
                        "/oauth2/authorization"
                );
        delegate.setAuthorizationRequestCustomizer(customizer ->
                customizer.additionalParameters(params -> params.put("prompt", "login"))
        );
        return delegate;
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            OAuth2AuthorizationRequestResolver kakaoAuthResolver,
            JwtAuthFilter jwtAuthFilter
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm
                        // OAuth2 로그인 시에만 세션이 필요하므로 IF_REQUIRED
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionFixation(sf -> sf.migrateSession())
                )
                .authorizeHttpRequests(auth -> auth
                        // OAuth2 시작 URL은 비로그인 사용자만 접근 허용
                        .requestMatchers("/oauth2/authorization/**").anonymous()

                        // 공개 엔드포인트
                        .requestMatchers(
                                "/", "/error",
                                "/health", "/actuator/**",
                                "/index.html",
                                "/swagger-ui.html", "/swagger-ui/**",
                                "/v3/api-docs", "/v3/api-docs/**",
                                // OAuth2 콜백·로그인 관련
                                "/oauth2/**", "/login/**",
                                // 로그인 URL/모바일 URL 노출(선택)
                                "/v1/auth/kakao/url", "/v1/auth/kakao/mobile-url",
                                // 토큰 교환 & 갱신 (Expo 전용 플로우)
                                "/v1/auth/token/exchange",
                                "/v1/auth/token/refresh"
                        ).permitAll()

                        // 보호 API
                        .requestMatchers("/users/me", "/v1/auth/kakao/logout").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/users/delete").authenticated()

                        // 그 외는 모두 인증
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .loginPage("/oauth2/authorization/kakao")
                        .authorizationEndpoint(ep -> ep.authorizationRequestResolver(kakaoAuthResolver))
                        .userInfoEndpoint(user -> user.userService(customOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler)
                )
                .logout(logout -> logout
                        .logoutUrl("/v1/auth/kakao/logout")
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler((req, res, auth) -> res.setStatus(204))
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> {
                            res.setStatus(401);
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write("{\"success\":false,\"message\":\"인증이 필요합니다.\"}");
                        })
                )
                // JWT 검증은 Username/Password 인증 필터보다 먼저
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();
        // 웹 프론트에서만 의미가 있음 (React Native는 CORS 미적용)
        c.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:5173",
                "https://growme-service.shop"
        ));
        c.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        c.setAllowedHeaders(List.of("*"));
        c.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", c);
        return src;
    }
}