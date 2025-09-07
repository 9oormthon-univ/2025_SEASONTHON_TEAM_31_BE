package com.cloudtone31.chat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MessageDTO {

    private String message;
    private String sender;
    private String timestamp;

}
