package com.japaneseLearning.dto;

public record KanjiDTO(
        Long kanjiId,
        String character,
        String onYomi,
        String kunYomi,
        String meaning,
        String hanjaMeaning
) {
}
