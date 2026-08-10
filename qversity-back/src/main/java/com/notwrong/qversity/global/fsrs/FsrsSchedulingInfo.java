package com.notwrong.qversity.global.fsrs;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class FsrsSchedulingInfo {
    private FsrsCard card;
    private FsrsRating rating;
    private Instant reviewDate;
}
