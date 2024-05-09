package com.intouch.aligooligo.seed.domain;

import lombok.Getter;

@Getter
public enum SeedState {
    SEED("씨앗"), STEM("줄기"), TREE("나무"), FRUITS("열매");

    private String state;

    SeedState(String state) {
        this.state = state;
    }
}
