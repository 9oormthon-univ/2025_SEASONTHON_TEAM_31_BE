package com.cloudtone31.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class NicknameReq {

    @NotBlank(message = "닉네임은 비워둘 수 없습니다.")
    @Size(min = 2, max = 20, message = "닉네임은 2~20자여야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣_\\-]+$", message = "닉네임은 한글/영문/숫자/_/-만 사용할 수 있습니다.")
    private String nickname;

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
}