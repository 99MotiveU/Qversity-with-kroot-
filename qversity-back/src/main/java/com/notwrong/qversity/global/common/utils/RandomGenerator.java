package com.notwrong.qversity.global.common.utils;

import java.util.Random;

public class RandomGenerator {

    private static final String[] ADJECTIVES = {
            "행복한", "즐거운", "밝은", "멋진", "귀여운", "용감한", "현명한", "친절한", "고요한", "빛나는"
    };

    private static final String[] NOUNS = {
            "사자", "호랑이", "코끼리", "기린", "고양이", "강아지", "여우", "늑대", "곰", "토끼"
    };

    private static final Random random = new Random();

    public static String generateRandomNickname() {
        String adjective = ADJECTIVES[random.nextInt(ADJECTIVES.length)];
        String noun = NOUNS[random.nextInt(NOUNS.length)];
        int number = random.nextInt(1000);
        return adjective + noun + number;
    }
}
