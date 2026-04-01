package com.japaneseLearning.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.japaneseLearning.entity.Kanji;
import com.japaneseLearning.entity.KanjiRadical;
import com.japaneseLearning.entity.KanjiStroke;
import com.japaneseLearning.entity.Lesson;
import com.japaneseLearning.repository.KanjiRadicalRepository;
import com.japaneseLearning.repository.KanjiRepository;
import com.japaneseLearning.repository.LessonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configuration
public class KanjiJsonSeeder {

    private static final Logger log = LoggerFactory.getLogger(KanjiJsonSeeder.class);

    private static final Map<String, KanjiMetadata> KANJI_METADATA = new HashMap<>();

    static {
        // Copied from legacy ASP.NET seed data so Spring Boot has the same baseline content.
        KANJI_METADATA.put("人", new KanjiMetadata("ジン、ニン", "ひと", "Nguoi", "NHAN", List.of(new RadicalMetadata("人", "NHAN"))));
        KANJI_METADATA.put("学", new KanjiMetadata("ガク", "まな.ぶ", "Hoc", "HOC", List.of(new RadicalMetadata("子", "TU"))));
        KANJI_METADATA.put("生", new KanjiMetadata("セイ", "い.きる", "Sinh", "SINH", List.of(new RadicalMetadata("生", "SINH"))));
        KANJI_METADATA.put("先", new KanjiMetadata("セン", "さき", "Truoc", "TIEN", List.of(new RadicalMetadata("儿", "NHI"))));
        KANJI_METADATA.put("社", new KanjiMetadata("シャ", "やしろ", "Xa", "XA", List.of(new RadicalMetadata("礻", "THI"))));
        KANJI_METADATA.put("医", new KanjiMetadata("イ", "", "Y (Bac si)", "Y", List.of(new RadicalMetadata("匚", "PHUONG"))));
        KANJI_METADATA.put("者", new KanjiMetadata("シャ", "もの", "Gia (nguoi)", "GIA", List.of(new RadicalMetadata("耂", "LAO"))));
        KANJI_METADATA.put("働", new KanjiMetadata("ドウ", "はたら.く", "Lam viec", "DONG", List.of(new RadicalMetadata("人", "NHAN"), new RadicalMetadata("生", "SINH"))));
        KANJI_METADATA.put("児", new KanjiMetadata("ジ", "こ", "Tre con", "NHI", List.of(new RadicalMetadata("儿", "NHI"), new RadicalMetadata("子", "TU"))));
        KANJI_METADATA.put("存", new KanjiMetadata("ソン", "", "Ton tai", "TON", List.of(new RadicalMetadata("子", "TU"), new RadicalMetadata("人", "NHAN"))));
    }

    @Bean
    CommandLineRunner seedKanjiFromJson(
            ObjectMapper objectMapper,
            ResourcePatternResolver resourcePatternResolver,
            KanjiRepository kanjiRepository,
            KanjiRadicalRepository kanjiRadicalRepository,
            LessonRepository lessonRepository) {
        return args -> {
            try {
                Resource[] resources = resourcePatternResolver.getResources("classpath:kanji/*.json");
                if (resources.length == 0) {
                    log.debug("No kanji JSON files found");
                    return;
                }

                Lesson defaultLesson = lessonRepository.findAll().stream().findFirst().orElse(null);
            int inserted = 0;
            int updated = 0;

            for (Resource resource : resources) {
                try (InputStream inputStream = resource.getInputStream()) {
                    JsonNode root = objectMapper.readTree(inputStream);
                    JsonNode kanjiArray = root.path("kanji");
                    if (!kanjiArray.isArray() || kanjiArray.isEmpty()) {
                        continue;
                    }

                    String character = kanjiArray.get(0).asText("").trim();
                    if (character.isEmpty()) {
                        continue;
                    }

                    Kanji kanji = kanjiRepository.findByCharacter(character);
                    boolean isNew = false;
                    if (kanji == null) {
                        kanji = new Kanji();
                        kanji.setCharacter(character);
                        isNew = true;
                    }
                    if (kanji.getLesson() == null) {
                        kanji.setLesson(defaultLesson);
                    }

                    KanjiMetadata metadata = KANJI_METADATA.get(character);
                    if (metadata != null) {
                        kanji.setOnYomi(metadata.onYomi);
                        kanji.setKunYomi(metadata.kunYomi);
                        kanji.setMeaning(metadata.meaning);
                        kanji.setHanjaMeaning(metadata.hanjaMeaning);

                        Set<KanjiRadical> radicals = new HashSet<>();
                        for (RadicalMetadata radicalMetadata : metadata.radicals) {
                            KanjiRadical radical = kanjiRadicalRepository.findByRadical(radicalMetadata.radical);
                            if (radical == null) {
                                radical = new KanjiRadical();
                                radical.setRadical(radicalMetadata.radical);
                                radical.setMeaning(radicalMetadata.meaning);
                                radical = kanjiRadicalRepository.save(radical);
                            }
                            radicals.add(radical);
                        }
                        kanji.setRadicals(radicals);
                    }

                    JsonNode strokesArray = root.path("strokes");
                    Set<KanjiStroke> strokes = new HashSet<>();
                    int strokeOrder = 1;

                    if (strokesArray.isArray()) {
                        for (JsonNode strokeNode : strokesArray) {
                            KanjiStroke stroke = new KanjiStroke();
                            stroke.setKanji(kanji);
                            stroke.setStrokeOrder(strokeOrder++);
                            stroke.setStrokeData(strokeNode.toString());
                            strokes.add(stroke);
                        }
                    }

                    kanji.setStrokes(strokes);
                    kanjiRepository.save(kanji);
                    if (isNew) {
                        inserted++;
                    } else {
                        updated++;
                    }
                } catch (Exception ex) {
                    log.warn("Skip invalid kanji JSON file {}: {}", resource.getFilename(), ex.getMessage());
                }
            }

            log.info("Kanji JSON seeding complete. inserted={}, updated={}", inserted, updated);
            } catch (Exception e) {
                log.warn("Error during kanji JSON seeding: {}", e.getMessage());
            }
        };
    }

    private record KanjiMetadata(
            String onYomi,
            String kunYomi,
            String meaning,
            String hanjaMeaning,
            List<RadicalMetadata> radicals) {
    }

    private record RadicalMetadata(
            String radical,
            String meaning) {
    }
}
