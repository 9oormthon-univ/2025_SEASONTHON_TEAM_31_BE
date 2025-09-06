package com.cloudtone31.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DangerResponseDTO {

    @JsonProperty("risk_level")
    private String riskLevel;
    private String message;
    private HelplineDTO helpline;

}
