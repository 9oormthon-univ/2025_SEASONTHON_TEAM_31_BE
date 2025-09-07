package com.cloudtone31.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatDataDTO {
    @JsonProperty("user_message") // JSON 필드 이름을 명시합니다.
    private MessageDTO userMessage;

    @JsonProperty("gpt_response")
    private MessageDTO gptResponse;
}
