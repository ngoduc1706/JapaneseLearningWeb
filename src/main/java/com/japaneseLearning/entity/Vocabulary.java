package com.japaneseLearning.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Vocabulary entity representing Japanese vocabulary items
 */
@Entity
@Table(name = "Vocabularies")
@NoArgsConstructor
@AllArgsConstructor
public class Vocabulary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "Kanji", nullable = false)
    private String kanji;

    @Column(name = "Hiragana", nullable = false)
    private String hiragana;

    @Column(name = "Romaji")
    private String romaji;

    @Column(name = "Meaning", columnDefinition = "LONGTEXT")
    private String meaning;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LessonId")
    private Lesson lesson;

    @ManyToMany
    @JoinTable(
        name = "VocabularyKanji",
        joinColumns = @JoinColumn(name = "VocabularyId"),
        inverseJoinColumns = @JoinColumn(name = "KanjiId")
    )
    @ToString.Exclude
    private Set<Kanji> kanjis = new HashSet<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getKanji() { return kanji; }
    public void setKanji(String kanji) { this.kanji = kanji; }

    public String getHiragana() { return hiragana; }
    public void setHiragana(String hiragana) { this.hiragana = hiragana; }

    public String getRomaji() { return romaji; }
    public void setRomaji(String romaji) { this.romaji = romaji; }

    public String getMeaning() { return meaning; }
    public void setMeaning(String meaning) { this.meaning = meaning; }

    public Lesson getLesson() { return lesson; }
    public void setLesson(Lesson lesson) { this.lesson = lesson; }

    public Set<Kanji> getKanjis() { return kanjis; }
    public void setKanjis(Set<Kanji> kanjis) { this.kanjis = kanjis; }
}
