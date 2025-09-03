package com.cloudtone31.dto;

import lombok.Getter;

@Getter
public class ChatResponseDTO {
    private String response;

    public ChatResponseDTO(String response) {
        this.response = response;
    }

}
