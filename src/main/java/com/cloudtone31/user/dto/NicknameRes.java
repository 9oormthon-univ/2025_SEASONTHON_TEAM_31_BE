package com.cloudtone31.user.dto;

public class NicknameRes {
    private final Long id;
    private final String nickname;

    public NicknameRes(Long id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }

    public Long getId() { return id; }
    public String getNickname() { return nickname; }
}