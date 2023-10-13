package me.levani.authserverextended.model.enums;

import lombok.Getter;

public enum CustomHeaders {
    CHALLENGE("request_challenge");
    @Getter
    private final String name;

    CustomHeaders(String name) {
        this.name = name;
    }
}
