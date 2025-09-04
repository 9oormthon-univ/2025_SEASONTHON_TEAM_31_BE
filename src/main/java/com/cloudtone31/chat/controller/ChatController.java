package com.cloudtone31.chat.controller;

import com.cloudtone31.chat.dto.ChatRequestDTO;
import com.cloudtone31.chat.dto.ChatResponseDTO;
import com.cloudtone31.chat.service.GptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat/messages")
public class ChatController {
    private final GptService gptService;

    @Autowired
    public ChatController(GptService gptService) {
        this.gptService = gptService;
    }

    @PostMapping
    public ChatResponseDTO chat(@RequestBody ChatRequestDTO requestDTO) {
        String gptResponse = gptService.getGptResponse(requestDTO.getMessage());
        return new ChatResponseDTO(gptResponse);
    }


}
