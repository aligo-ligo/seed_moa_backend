package com.intouch.aligooligo.domain.member.entity;

public enum Role {
    MEMBER("유저"), GUEST("비회원");

    private String value;

    Role(String value) {
        this.value = value;
    }
}
