package com.japaneseLearning.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

/**
 * KanjiRadical entity representing Kanji radicals used in character composition
 */
@Entity
@Table(name = "KanjiRadicals")
@NoArgsConstructor
@AllArgsConstructor
public class KanjiRadical {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "KanjiRadicalId")
    private Long kanjiRadicalId;

    @Column(name = "Radical", nullable = false)
    private String radical;

    @Column(name = "Meaning", columnDefinition = "LONGTEXT")
    private String meaning;

    @ManyToMany(mappedBy = "radicals")
    @ToString.Exclude
    private Set<Kanji> kanjis = new HashSet<>();

    public Long getKanjiRadicalId() { return kanjiRadicalId; }
    public void setKanjiRadicalId(Long kanjiRadicalId) { this.kanjiRadicalId = kanjiRadicalId; }

    public String getRadical() { return radical; }
    public void setRadical(String radical) { this.radical = radical; }

    public String getMeaning() { return meaning; }
    public void setMeaning(String meaning) { this.meaning = meaning; }

    public Set<Kanji> getKanjis() { return kanjis; }
    public void setKanjis(Set<Kanji> kanjis) { this.kanjis = kanjis; }
}
