package com.cloudtone31.auth;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;
import java.util.Map;

@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // @LoginUser 어노테이션이 있는지 확인
        boolean hasLoginUserAnnotation = parameter.hasParameterAnnotation(LoginUser.class);
        // 파라미터 타입이 String인지 확인
        boolean isStringType = (parameter.getParameterType().equals(String.class));
        return hasLoginUserAnnotation && isStringType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        // Spring Security 컨텍스트에서 Authentication 객체를 가져옵니다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return resolveKakaoId(authentication);
    }


    /** Authentication에서 kakaoId 추출 (JWT/세션 둘 다 지원) */
    private String resolveKakaoId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null; // 또는 예외 처리
        }

        Object principal = authentication.getPrincipal();

        // 1) JWT 필터가 principal을 kakaoId(String)으로 넣는 경우
        if (principal instanceof String s && !s.isBlank()) {
            return s;
        }

        // 2) 세션(OAuth2) 로그인인 경우
        if (principal instanceof OAuth2User o) {
            return extractKakaoIdFromAttributes(o.getAttributes());
        }

        // 익명 사용자인 경우 (principal이 "anonymousUser" 문자열일 수 있음)
        if ("anonymousUser".equals(principal)) {
            return null;
        }

        // 3) 필요하면 커스텀 Principal 타입도 처리
        // if (principal instanceof JwtUserPrincipal p) return p.getKakaoId();

        return null;
    }

    /** OAuth2 attributes에서 Kakao ID를 문자열로 추출 */
    private String extractKakaoIdFromAttributes(Map<String, Object> attributes) {
        // id 혹은 sub 클레임을 우선적으로 확인 (표준 스펙)
        for (String key : List.of("id", "sub", "kakaoId", "kakao_id")) {
            Object v = attributes.get(key);
            if (v == null) continue;
            if (v instanceof String s && !s.isBlank()) return s;
            if (v instanceof Number n) return String.valueOf(n.longValue());
            return String.valueOf(v);
        }
        return null;
    }



}
