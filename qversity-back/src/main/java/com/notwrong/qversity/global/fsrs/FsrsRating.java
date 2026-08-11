package com.notwrong.qversity.global.fsrs;

import lombok.Getter;

@Getter
public enum FsrsRating {
    AGAIN(1), HARD(2), GOOD(3), EASY(4);

    private final int value;

    FsrsRating(int value) {
        this.value = value;
    }

    public static FsrsRating from(int value) {
        for (FsrsRating r : values()) {
            if (r.value == value) return r;
        }
        throw new IllegalArgumentException("Invalid rating: " + value);
    }
}
