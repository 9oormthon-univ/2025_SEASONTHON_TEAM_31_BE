package com.cloudtone31.chat.controller;

import com.cloudtone31.chat.dto.*;
import com.cloudtone31.chat.service.GptService;
import com.cloudtone31.chat.service.RiskAssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/chat/messages")
public class ChatController {
    private final GptService gptService;
    private final RiskAssessmentService riskAssessmentService;

    @Autowired
    public ChatController(GptService gptService, RiskAssessmentService riskAssessmentService) {
        this.gptService = gptService;
        this.riskAssessmentService = riskAssessmentService;
    }

    @PostMapping
    public ResponseEntity<?> chat(@RequestBody ChatRequestDTO requestDTO) {
        RiskAssessmentService.RiskLevel riskLevel = riskAssessmentService.assessRisk(requestDTO.getMessage());

        if(riskLevel == RiskAssessmentService.RiskLevel.DANGER){
            HelplineDTO helpline = HelplineDTO.builder()
                    .suicidePrevention("1577-0199")
                    .youthCounseling("1388")
                    .build();

            DangerResponseDTO dangerResponse = DangerResponseDTO.builder()
                    .riskLevel("danger")
                    .message("지금 힘든 상황이시군요. 전문가와 상담해보시는 것이 어떨까요?")
                    .helpline(helpline)
                    .build();
            return ResponseEntity.ok(dangerResponse);

        }

        if (riskLevel == RiskAssessmentService.RiskLevel.WARNING) {
            return ResponseEntity.ok(ApiResponseDTO.<String>builder()
                    .success(true)
                    .data("많이 힘드시군요. 혼자 고민하기보다 전문가의 도움을 받아보는 것을 권해드립니다.")
                    .message("전문 상담 권유")
                    .build());
        }

        String gptResponse = gptService.getGptResponse(requestDTO.getMessage());

        MessageDTO userMessage = MessageDTO.builder()
                .message(requestDTO.getMessage())
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

        return ResponseEntity.ok(ApiResponseDTO.<ChatDataDTO>builder()
                .success(true)
                .data(chatData)
                .message("메시지가 전송되었습니다.")
                .build());
    }


}
