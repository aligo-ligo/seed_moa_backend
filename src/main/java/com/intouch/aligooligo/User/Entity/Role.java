package com.intouch.aligooligo.User.Entity;


public enum Role {
    USER("회원"), GUEST("비회원");

    private String name;

    Role(String name) {
        this.name = name;
    }

}
