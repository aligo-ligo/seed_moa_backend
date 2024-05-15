package com.intouch.aligooligo.seed.domain;

import lombok.Getter;

@Getter
public enum SeedState {
    SEED("씨앗", 30), STEM("줄기", 60), TREE("나무", 90), FRUITS("열매", 100);

    private String state;
    private Integer boundary;

    SeedState(String state, Integer boundary) {
        this.state = state;
        this.boundary = boundary;
    }
}
