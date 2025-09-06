package com.cloudtone31.chat.controller;

import com.cloudtone31.auth.LoginUser;
import com.cloudtone31.chat.dto.*;
import com.cloudtone31.chat.service.GptService;
import com.cloudtone31.chat.service.RiskAssessmentService;
import com.cloudtone31.global.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping(value = "/chat/messages", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ChatController {

    private final GptService gptService;
    private final RiskAssessmentService riskAssessmentService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> chat(
            @LoginUser String userIdStr,   // JWT의 sub(userId)를 문자열로 받음
            @RequestBody ChatRequestDTO requestDTO
    ) {
        // 1) 인증 확인
        Long userId = parseUserIdOr401(userIdStr);

        // 2) 입력 검증
        if (requestDTO == null || !StringUtils.hasText(requestDTO.getMessage())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("message는 비어 있을 수 없습니다."));
        }
        String message = requestDTO.getMessage().trim();
        if (message.length() > 2_000) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("message가 너무 깁니다. (최대 2000자)"));
        }

        // 3) 위험도 평가
        RiskAssessmentService.RiskLevel riskLevel = riskAssessmentService.assessRisk(message);

        if (riskLevel == RiskAssessmentService.RiskLevel.DANGER) {
            HelplineDTO helpline = HelplineDTO.builder()
                    .suicidePrevention("1577-0199")
                    .youthCounseling("1388")
                    .build();

            DangerResponseDTO dangerResponse = DangerResponseDTO.builder()
                    .riskLevel("danger")
                    .message("지금 매우 힘든 상황이 느껴져요. 가까운 분께 도움을 요청하거나 전문 상담기관과 연결을 권해드릴게요.")
                    .helpline(helpline)
                    .build();

            // 필요시 userId로 기록/알림 로깅 등 서비스 호출 가능
            return ResponseEntity.ok(dangerResponse);
        }

        if (riskLevel == RiskAssessmentService.RiskLevel.WARNING) {
            return ResponseEntity.ok(ApiResponse.ok(
                    "많이 힘드시군요. 혼자 고민하기보다 전문가의 도움을 받아보는 것을 권해드립니다.",
                    "전문 상담 권유"
            ));
        }

        // 4) GPT 응답 (필요하면 userId 전달해 개인화/컨텍스트 저장)
        String gptResponse = gptService.getGptResponse(message);

        MessageDTO userMessage = MessageDTO.builder()
                .message(message)
                .sender("user")
                .timestamp(Instant.now().toString())
                .build();

        MessageDTO gptMessage = MessageDTO.builder()
                .message(gptResponse)
                .sender("gpt")
                .timestamp(Instant.now().toString())
                .build();

        ChatDataDTO chatData = ChatDataDTO.builder()
                .userMessage(userMessage)
                .gptResponse(gptMessage)
                .build();

        return ResponseEntity.ok(ApiResponse.ok(chatData, "메시지가 전송되었습니다."));
    }

    /** userId 문자열을 Long으로 파싱, 실패 시 401로 처리하기 위한 헬퍼 */
    private Long parseUserIdOr401(String userIdStr) {
        if (!StringUtils.hasText(userIdStr)) {
            throw new UnauthorizedException("인증이 필요합니다."); // GlobalExceptionHandler에서 401 매핑 가정
        }
        try {
            return Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            throw new UnauthorizedException("잘못된 인증 주체(sub) 형식입니다.");
        }
    }

    // 간단한 401용 런타임 예외 (프로젝트 공통 예외가 있다면 그걸 사용)
    static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) { super(message); }
    }
}