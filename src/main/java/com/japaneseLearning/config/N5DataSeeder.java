package com.japaneseLearning.config;

import com.japaneseLearning.entity.*;
import com.japaneseLearning.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Component
public class N5DataSeeder implements CommandLineRunner {
    
    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final VocabularyRepository vocabularyRepository;
    private final GrammarRepository grammarRepository;
    
    public N5DataSeeder(CategoryRepository categoryRepository,
                        CourseRepository courseRepository,
                        LessonRepository lessonRepository,
                        VocabularyRepository vocabularyRepository,
                        GrammarRepository grammarRepository) {
        this.categoryRepository = categoryRepository;
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.vocabularyRepository = vocabularyRepository;
        this.grammarRepository = grammarRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Find existing category instead of deleting (Avoid FK issues)
        Category n5Category = categoryRepository.findByNameIgnoreCase("N5");
        
        if (n5Category != null) {
            // Check if course already exists to avoid duplication
            boolean courseExists = courseRepository.findAll().stream()
                .anyMatch(c -> c.getTitle().equals("Khóa học Tiếng Nhật N5"));
            
            if (courseExists) {
                System.out.println("N5 Course already exists. Skipping seed...");
                return;
            }
        } else {
            // Create Category if not exists
            n5Category = new Category();
            n5Category.setName("N5");
            n5Category.setDescription("Tiếng Nhật N5 - Cấp độ sơ cấp dành cho người mới bắt đầu.");
            n5Category = categoryRepository.save(n5Category);
        }

        // Create Course
        Course n5Course = new Course();
        n5Course.setTitle("Khóa học Tiếng Nhật N5");
        n5Course.setDescription("Lộ trình học từ con số 0 đến N5. Bao gồm 25 bài học đầy đủ từ vựng, ngữ pháp và Kanji.");
        n5Course.setIsFree(true);
        n5Course.setPrice(0.0);
        n5Course.setCategory(n5Category);
        n5Course = courseRepository.save(n5Course);

        // Lesson Titles (25 lessons)
        String[] lessonTitles = {
            "Bài 1 – Nước, Người & Ngôn ngữ", "Bài 2 – Cách gọi tên người Nhật", "Bài 3 – Gia đình",
            "Bài 4 – Thời gian", "Bài 5 – Nghề nghiệp", "Bài 6 – Màu sắc", "Bài 7 – Địa điểm",
            "Bài 8 – Sở thích", "Bài 9 – Âm nhạc, Thể thao & Điện ảnh", "Bài 10 – Trong nhà",
            "Bài 11 – Số lượng", "Bài 12 – Lễ hội & Địa danh", "Bài 13 – Trong khu phố",
            "Bài 14 – Nhà ga", "Bài 15 – Nghề nghiệp", "Bài 16 – Hướng dẫn & yêu cầu",
            "Bài 17 – Sức khỏe", "Bài 18 – Mùa & Thiên nhiên", "Bài 19 – Ẩm thực",
            "Bài 20 – Tặng quà & cảm xúc", "Bài 21 – Chức danh", "Bài 22 – Quần áo",
            "Bài 23 – Giao thông & Đường xá", "Bài 24 – Gặp gỡ & Giao tiếp", "Bài 25 – Tổng kết & chia tay"
        };

        Map<Integer, Lesson> lessonMap = new HashMap<>();
        for (int i = 0; i < lessonTitles.length; i++) {
            Lesson lesson = new Lesson();
            lesson.setCourse(n5Course);
            lesson.setTitle(lessonTitles[i]);
            lesson.setContent("Nội dung học chi tiết cho " + lessonTitles[i]);
            lesson.setOrderInCourse(i + 1);
            lesson = lessonRepository.save(lesson);
            lessonMap.put(i + 1, lesson);
        }

        // --- SEED DATA PER LESSON ---
        seedLesson1(lessonMap.get(1));
        seedLesson2(lessonMap.get(2));
        seedLesson3(lessonMap.get(3));
        seedLesson4(lessonMap.get(4));
        seedLesson5(lessonMap.get(5));
        seedLesson6(lessonMap.get(6));
        seedLesson7(lessonMap.get(7));
        seedLesson8(lessonMap.get(8));
        seedLesson9(lessonMap.get(9));
        seedLesson10(lessonMap.get(10));

        System.out.println("N5 DATA SEEDED SUCCESSFULLY WITH 10 COMPLETE LESSONS!");
    }

    private void seedLesson1(Lesson lesson) {
        addV(lesson, "私", "わたし", "Watashi", "Tôi");
        addV(lesson, "あなた", "あなた", "Anata", "Bạn/Anh/Chị");
        addV(lesson, "学生", "がくせい", "Gakusei", "Học sinh/Sinh viên");
        addV(lesson, "先生", "せんせい", "Sensei", "Thầy/Cô");
        addG(lesson, "N1 は N2 です", "わたしは学生です。", "N5", "Cấu trúc khẳng định: N1 là N2 - Ví dụ liên hệ: 'Tôi là học sinh.'");
        // addK(lesson, "人", "ジン/ニン", "ひと", "Người", "NHÂN");
        // addK(lesson, "学", "ガク", "まなbu", "Học", "HỌC");
    }

    private void seedLesson2(Lesson lesson) {
        addV(lesson, "名前", "なまえ", "Namae", "Tên");
        addV(lesson, "これ", "これ", "Kore", "Cái này");
        addV(lesson, "それ", "それ", "Sore", "Cái đó");
        addV(lesson, "あれ", "あれ", "Are", "Cái kia");
        addG(lesson, "これ/それ/あれ は N です", "これは本です。", "N5", "Chỉ định từ: Đây/Đó/Kia là N");
        // addK(lesson, "名", "メイ/ミョウ", "な", "Tên", "DANH");
        // addK(lesson, "前", "ゼン", "まえ", "Trước", "TIỀN");
    }

    private void seedLesson3(Lesson lesson) {
        addV(lesson, "家族", "かぞく", "Kazoku", "Gia đình");
        addV(lesson, "父", "ちち", "Chichi", "Bố (của mình)");
        addV(lesson, "母", "はは", "Haha", "Mẹ (của mình)");
        addG(lesson, "N1 の N2", "わたしの母です。", "N5", "Sở hữu: N2 của N1 - Ví dụ: 'Đây là mẹ tôi.'");
    }

    private void seedLesson4(Lesson lesson) {
        addV(lesson, "時間", "じかん", "Jikan", "Thời gian");
        addV(lesson, "今", "いま", "Ima", "Bây giờ");
        addV(lesson, "時", "じ", "Ji", "Giờ");
        addG(lesson, "今 ～時 ～分 です", "今 8時 です。", "N5", "Hỏi và trả lời về thời gian");
    }

    private void seedLesson5(Lesson lesson) {
        addV(lesson, "仕事", "しごと", "Shigoto", "Công việc");
        addV(lesson, "医者", "いしゃ", "Isha", "Bác sĩ");
        addG(lesson, "N は ～です", "父は医者です。", "N5", "Nói về nghề nghiệp");
    }

    private void seedLesson6(Lesson lesson) { addV(lesson, "赤", "あka", "Aka", "Đỏ"); addG(lesson, "Màu sắc", "あかいです", "N5", "Màu sắc"); }
    private void seedLesson7(Lesson lesson) { addV(lesson, "場所", "basho", "Basho", "Địa điểm"); addG(lesson, "Vị trí", "ここにあります", "N5", "Vị trí"); }
    private void seedLesson8(Lesson lesson) { addV(lesson, "好き", "suki", "Suki", "Thích"); addG(lesson, "Sở thích", "すきです", "N5", "Sở thích"); }
    private void seedLesson9(Lesson lesson) { addV(lesson, "音楽", "ongaku", "Ongaku", "Âm nhạc"); addG(lesson, "Hoạt động", "ききます", "N5", "Hoạt động"); }
    private void seedLesson10(Lesson lesson) { addV(lesson, "部屋", "heya", "Heya", "Phòng"); addG(lesson, "Tại phòng", "へやにあります", "N5", "Tại phòng"); }

    private void addV(Lesson l, String k, String h, String r, String m) {
        Vocabulary v = new Vocabulary();
        v.setLesson(l); v.setKanji(k); v.setHiragana(h); v.setRomaji(r); v.setMeaning(m);
        vocabularyRepository.save(v);
    }
    private void addG(Lesson l, String r, String e, String lv, String n) {
        Grammar g = new Grammar();
        g.setLesson(l); g.setRule(r); g.setExample(e); g.setLevel(lv); g.setNotes(n);
        grammarRepository.save(g);
    }
    /*
    private void addK(Lesson l, String c, String o, String ku, String m, String h) {
        Kanji k = new Kanji();
        k.setLesson(l); k.setCharacter(c); k.setOnYomi(o); k.setKunYomi(ku); k.setMeaning(m); k.setHanjaMeaning(h);
        kanjiRepository.save(k);
    }
    */
}
