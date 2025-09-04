package com.cloudtone31.auth;

import java.util.Map;

public class KakaoAttributes {
    public static Long getId(Map<String, Object> attrs) {
        Object id = attrs.get("id");
        return id == null ? null : Long.valueOf(String.valueOf(id));
    }
    @SuppressWarnings("unchecked")
    public static String getEmail(Map<String, Object> attrs) {
        Map<String, Object> acc = (Map<String, Object>) attrs.get("kakao_account");
        return acc == null ? null : (String) acc.get("email");
    }
    @SuppressWarnings("unchecked")
    public static String getNickname(Map<String, Object> attrs) {
        Map<String, Object> acc = (Map<String, Object>) attrs.get("kakao_account");
        if (acc == null) return null;
        Map<String, Object> profile = (Map<String, Object>) acc.get("profile");
        return profile == null ? null : (String) profile.get("nickname");
    }
    @SuppressWarnings("unchecked")
    public static String getProfileImage(Map<String, Object> attrs) {
        Map<String, Object> acc = (Map<String, Object>) attrs.get("kakao_account");
        if (acc == null) return null;
        Map<String, Object> profile = (Map<String, Object>) acc.get("profile");
        return profile == null ? null : (String) profile.get("profile_image_url");
    }
}