package com.cloudtone31.global;

import com.cloudtone31.auth.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7); // "Bearer " 이후
        try {
            var jws = jwtService.parse(token);
            Claims claims = jws.getBody();

            // typ=access 만 허용
            if (!"access".equals(claims.get("typ"))) {
                chain.doFilter(request, response);
                return;
            }

            String userId = claims.getSubject(); // sub
            var auth = new UsernamePasswordAuthenticationToken(
                    userId, null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (JwtException e) {
            // 토큰이 이상하면 그냥 인증 없이 통과 -> 최종적으로 401 처리됨
            SecurityContextHolder.clearContext();
        }

        chain.doFilter(request, response);
    }
}