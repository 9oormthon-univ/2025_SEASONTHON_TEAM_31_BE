//package com.cloudtone31.global.config;
//
//import com.cloudtone31.user.domain.User;
//import lombok.Getter;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//
//import java.util.Collection;
//import java.util.Map;
//
//@Getter
//public class PrincipalDetails implements OAuth2User {
//
//    private final User user;
//    private final Map<String, Object> attributes;
//
//    /**
//     * OAuth2 로그인을 할 때 사용되는 생성자입니다.
//     * @param user 우리 애플리케이션의 DB에 저장된 사용자 엔티티
//     * @param attributes OAuth2 공급자(예: 카카오)로부터 받은 사용자 정보
//     */
//    public PrincipalDetails(User user, Map<String, Object> attributes) {
//        this.user = user;
//        this.attributes = attributes;
//    }
//
//    // OAuth2User 인터페이스의 필수 구현 메서드들
//
//    @Override
//    public Map<String, Object> getAttributes() {
//        return attributes;
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        // 현재는 권한(Role)을 사용하지 않으므로 null을 반환합니다.
//        // 권한 관리가 필요하다면 여기서 User의 Role을 기반으로 설정해야 합니다.
//        return null;
//    }
//
//    @Override
//    public String getName() {
//        // OAuth2 공급자가 제공하는 사용자의 고유 ID를 반환합니다.
//        // 카카오의 경우 "id" 속성에 고유 번호가 담겨있습니다.
//        return String.valueOf(attributes.get("id"));
//    }
//}
//
