package com.cloudtone31.chat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiResponseDTO<T> {

    private boolean success;
    private T data;
    private String message;

}
