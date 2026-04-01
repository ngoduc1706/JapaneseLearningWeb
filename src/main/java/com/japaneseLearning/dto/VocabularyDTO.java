package com.japaneseLearning.dto;

public class VocabularyDTO {
    private Long id;
    private String kanji;
    private String hiragana;
    private String romaji;
    private String meaning;
    private Long lessonId;

    public VocabularyDTO() {
    }

    public VocabularyDTO(Long id, String kanji, String hiragana, String romaji, String meaning, Long lessonId) {
        this.id = id;
        this.kanji = kanji;
        this.hiragana = hiragana;
        this.romaji = romaji;
        this.meaning = meaning;
        this.lessonId = lessonId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKanji() {
        return kanji;
    }

    public void setKanji(String kanji) {
        this.kanji = kanji;
    }

    public String getHiragana() {
        return hiragana;
    }

    public void setHiragana(String hiragana) {
        this.hiragana = hiragana;
    }

    public String getRomaji() {
        return romaji;
    }

    public void setRomaji(String romaji) {
        this.romaji = romaji;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public Long getLessonId() {
        return lessonId;
    }

    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }
}
