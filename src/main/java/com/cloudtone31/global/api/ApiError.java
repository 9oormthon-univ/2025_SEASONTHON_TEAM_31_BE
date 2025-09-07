package com.cloudtone31.global.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;

/** 에러 상세 정보를 담고 싶을 때 사용(선택). */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        String code,           // 예: "BAD_REQUEST", "NOT_FOUND"
        String message,        // 사용자 메시지
        Map<String, Object> meta // 필드 에러 등 부가정보
) {}