package com.japaneseLearning.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Kanji entity representing individual Kanji characters
 */
@Entity
@Table(name = "Kanjis")
@NoArgsConstructor
@AllArgsConstructor
public class Kanji {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "kanji_character", nullable = false)
    private String character;

    @Column(name = "OnYomi", columnDefinition = "LONGTEXT")
    private String onYomi;

    @Column(name = "KunYomi", columnDefinition = "LONGTEXT")
    private String kunYomi;

    @Column(name = "Meaning", columnDefinition = "LONGTEXT")
    private String meaning;

    @Column(name = "HanjaMeaning")
    private String hanjaMeaning;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LessonId")
    @JsonIgnore
    private Lesson lesson;

    @ManyToMany(mappedBy = "kanjis")
    @ToString.Exclude
    @JsonIgnore
    private Set<Vocabulary> vocabularies = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "KanjiKanjiRadical",
        joinColumns = @JoinColumn(name = "KanjiId"),
        inverseJoinColumns = @JoinColumn(name = "KanjiRadicalId")
    )
    @ToString.Exclude
    @JsonIgnore
    private Set<KanjiRadical> radicals = new HashSet<>();

    @OneToMany(mappedBy = "kanji", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnore
    private Set<KanjiStroke> strokes = new HashSet<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCharacter() { return character; }
    public void setCharacter(String character) { this.character = character; }

    public String getOnYomi() { return onYomi; }
    public void setOnYomi(String onYomi) { this.onYomi = onYomi; }

    public String getKunYomi() { return kunYomi; }
    public void setKunYomi(String kunYomi) { this.kunYomi = kunYomi; }

    public String getMeaning() { return meaning; }
    public void setMeaning(String meaning) { this.meaning = meaning; }

    public String getHanjaMeaning() { return hanjaMeaning; }
    public void setHanjaMeaning(String hanjaMeaning) { this.hanjaMeaning = hanjaMeaning; }

    public Lesson getLesson() { return lesson; }
    public void setLesson(Lesson lesson) { this.lesson = lesson; }

    public Set<Vocabulary> getVocabularies() { return vocabularies; }
    public void setVocabularies(Set<Vocabulary> vocabularies) { this.vocabularies = vocabularies; }

    public Set<KanjiRadical> getRadicals() { return radicals; }
    public void setRadicals(Set<KanjiRadical> radicals) { this.radicals = radicals; }

    public Set<KanjiStroke> getStrokes() { return strokes; }
    public void setStrokes(Set<KanjiStroke> strokes) { this.strokes = strokes; }
}
