package com.intouch.aligooligo.seed.domain;

public enum SeedState {
    SEED("씨앗"), STEM("줄기"), TREE("나무"), FRUIT("열매");

    private String state;

    public String getState() {
        return state;
    }
}
