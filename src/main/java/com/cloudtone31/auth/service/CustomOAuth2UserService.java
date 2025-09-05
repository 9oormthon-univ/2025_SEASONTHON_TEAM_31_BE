package com.cloudtone31.auth.service;

import com.cloudtone31.auth.KakaoAttributes;
import com.cloudtone31.user.domain.User;
import com.cloudtone31.user.repository.UserLoginRepository;
import com.cloudtone31.userplants.domain.PlantType;
import com.cloudtone31.userplants.domain.UserPlants;
import com.cloudtone31.userplants.repository.PlantTypeRepository;
import com.cloudtone31.userplants.repository.UserPlantsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserLoginRepository userLoginRepository;
    private final UserPlantsRepository userPlantsRepository; // 추가
    private final PlantTypeRepository plantTypeRepository; // 추가

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attrs = oAuth2User.getAttributes();

        // ⚠️ kakaoId는 DB에서 VARCHAR이므로 문자열로 저장
        String kakaoId = String.valueOf(KakaoAttributes.getId(attrs));
        String email = KakaoAttributes.getEmail(attrs);
        String nickname = KakaoAttributes.getNickname(attrs);
        String profile = KakaoAttributes.getProfileImage(attrs);

        // name은 NOT NULL 제약 → 최소한 닉네임으로 대체
        String name = (nickname != null && !nickname.isBlank()) ? nickname : "KakaoUser";

        // Upsert
        User user = userLoginRepository.findByKakaoId(kakaoId)
                .map(u -> {
                    // 기존 유저: 마지막 로그인 시간 갱신 + 닉네임이 오면 업데이트
                    u.updateLastLoginAt(LocalDateTime.now());
                    if (nickname != null && !nickname.isBlank()) {
                        u.updateNickname(nickname);
                    }
                    // (선택) email/profileImage도 갱신하고 싶으면
                    // User 엔티티에 updateEmail/updateProfileImage 같은 메서드를 추가해서 호출하세요.
                    return userLoginRepository.save(u);
                })
                .orElseGet(() -> { // 이 부분 수정됨
                    User newUser = User.builder()
                            .kakaoId(kakaoId)      // String!
                            .email(email)
                            .nickname(nickname)
                            .name(name)            // 🔴 NOT NULL 필수
                            .profileImage(profile)
                            .lastLoginAt(LocalDateTime.now())
                            .build();
                    userLoginRepository.save(newUser);
                    PlantType firstPlantType = plantTypeRepository.findAllByOrderByUnlockOrderAsc().get(0);
                    UserPlants firstPlant = UserPlants.builder()
                            .user(newUser)
                            .plantType(firstPlantType.getTypeCode())
                            .plantName("나의 " + firstPlantType.getName())
                            .growthPercentage(0)
                            .plantImage(String.format("/images/%s_stage0.png", firstPlantType.getTypeCode()))
                            .growthStage("씨앗")
                            .isActive(true)
                            .build();
                    userPlantsRepository.save(firstPlant);
                    return newUser;
                });

        // Security 컨텍스트에 전달
        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                attrs,
                "id" // nameAttributeKey (카카오의 주 식별자 키)
        );
    }
}