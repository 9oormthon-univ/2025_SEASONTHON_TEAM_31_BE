package com.cloudtone31.chat.controller;

import com.cloudtone31.chat.dto.*;
import com.cloudtone31.chat.service.GptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/chat/messages")
public class ChatController {
    private final GptService gptService;

    @Autowired
    public ChatController(GptService gptService) {
        this.gptService = gptService;
    }

    @PostMapping
    public ApiResponseDTO<ChatDataDTO> chat(@RequestBody ChatRequestDTO requestDTO) {
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

        return ApiResponseDTO.<ChatDataDTO>builder()
                .success(true)
                .data(chatData)
                .message("메시지가 전송되었습니다.")
                .build();
    }


}
