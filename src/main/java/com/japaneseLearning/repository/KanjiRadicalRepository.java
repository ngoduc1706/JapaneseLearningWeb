package com.japaneseLearning.repository;

import com.japaneseLearning.entity.KanjiRadical;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KanjiRadicalRepository extends JpaRepository<KanjiRadical, Long> {
    KanjiRadical findByRadical(String radical);

    /** Eagerly fetch associated Kanjis to avoid LazyInitializationException (open-in-view=false) */
    @Query("SELECT DISTINCT r FROM KanjiRadical r LEFT JOIN FETCH r.kanjis ORDER BY r.kanjiRadicalId ASC")
    List<KanjiRadical> findAllWithKanjis();
}
