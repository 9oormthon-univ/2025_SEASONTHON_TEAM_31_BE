package com.cloudtone31.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HelplineDTO {

    @JsonProperty("suicide_prevention")
    private String suicidePrevention;

    @JsonProperty("youth_counseling")
    private String youthCounseling;

}
