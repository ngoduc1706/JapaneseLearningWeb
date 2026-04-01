package com.japaneseLearning.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

/**
 * KanjiStroke entity representing individual strokes in Kanji characters
 */
@Entity
@Table(name = "KanjiStrokes")
@NoArgsConstructor
@AllArgsConstructor
public class KanjiStroke {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KanjiId", nullable = false)
    @JsonIgnore
    private Kanji kanji;

    @Column(name = "StrokeOrder")
    private Integer strokeOrder;

    @Column(name = "StrokeData", columnDefinition = "LONGTEXT")
    private String strokeData;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Kanji getKanji() { return kanji; }
    public void setKanji(Kanji kanji) { this.kanji = kanji; }

    public Integer getStrokeOrder() { return strokeOrder; }
    public void setStrokeOrder(Integer strokeOrder) { this.strokeOrder = strokeOrder; }

    public String getStrokeData() { return strokeData; }
    public void setStrokeData(String strokeData) { this.strokeData = strokeData; }
}
