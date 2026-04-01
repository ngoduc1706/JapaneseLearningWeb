package com.japaneseLearning.config;

import com.japaneseLearning.entity.*;
import com.japaneseLearning.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Component
public class N4DataSeeder implements CommandLineRunner {
    
    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final VocabularyRepository vocabularyRepository;
    private final GrammarRepository grammarRepository;
    
    public N4DataSeeder(CategoryRepository categoryRepository,
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
        Category n4Category = categoryRepository.findByNameIgnoreCase("N4");
        
        if (n4Category == null) {
            n4Category = new Category();
            n4Category.setName("N4");
            n4Category.setDescription("Tiếng Nhật N4 - Cấp độ sơ cấp nâng cao cho học viên đã hoàn thành N5.");
            n4Category = categoryRepository.save(n4Category);
        }
        
        // Create Course if not exists
        Course n4Course;
        Optional<Course> existingCourse = courseRepository.findAll().stream()
                .filter(c -> c.getTitle().equals("Khóa học Tiếng Nhật N4"))
                .findFirst();
        
        if (existingCourse.isPresent()) {
            n4Course = existingCourse.get();
            System.out.println("N4 Course found. Updating...");
        } else {
            n4Course = new Course();
            n4Course.setTitle("Khóa học Tiếng Nhật N4");
            System.out.println("N4 Course not found. Creating...");
        }

        n4Course.setDescription("Lộ trình nâng cao từ N5. 20 bài học chi tiết với từ vựng, ngữ pháp và Kanji.");
        n4Course.setIsFree(false);
        n4Course.setPrice(299000.0);
        n4Course.setCategory(n4Category);
        n4Course = courseRepository.save(n4Course);

        // Only seed lessons if none exist yet for this course
        long existingLessons = lessonRepository.countByCourse_CourseId(n4Course.getCourseId());
        if (existingLessons > 0) {
            System.out.println("N4 lessons already seeded (" + existingLessons + " lessons). Skipping.");
            return;
        }

        String[] lessonTitles = {
            "Bài 1 – Sở thích và thói quen", "Bài 2 – Du lịch và phương tiện giao thông",
            "Bài 3 – Mua sắm và giá cả", "Bài 4 – Sức khỏe và bệnh tật", "Bài 5 – Công việc và sự nghiệp",
            "Bài 6 – Ăn uống và nhà hàng", "Bài 7 – Quần áo và thời trang", "Bài 8 – Thời tiết và mùa",
            "Bài 9 – Giáo dục và trường học", "Bài 10 – Lễ tết và ngày lễ"
        };

        Map<Integer, Lesson> lessonMap = new HashMap<>();
        for (int i = 0; i < lessonTitles.length; i++) {
            Lesson lesson = new Lesson();
            lesson.setCourse(n4Course);
            lesson.setTitle(lessonTitles[i]);
            lesson.setContent("Nội dung N4 cho " + lessonTitles[i]);
            lesson.setOrderInCourse(i + 1);
            lesson = lessonRepository.save(lesson);
            lessonMap.put(i + 1, lesson);
        }

        seedLesson1(lessonMap.get(1));
        seedLesson2(lessonMap.get(2));

        System.out.println("N4 DATA SEEDED SUCCESSFULLY!");
    }

    private void seedLesson1(Lesson lesson) {
        // Sở thích và thói quen
        addV(lesson, "好き", "すき", "Suki", "Thích");
        addV(lesson, "趣味", "しゅみ", "Shumi", "Sở thích");
        addV(lesson, "毎日", "まいにち", "Mainichi", "Mỗi ngày");
        addV(lesson, "運動", "うんどう", "Undou", "Thể dục");
        addV(lesson, "読書", "どくしょ", "Dokusho", "Đọc sách");
        addV(lesson, "映画", "えいが", "Eiga", "Phim");
        addV(lesson, "音楽", "おんがく", "Ongaku", "Âm nhạc");
        addV(lesson, "絵を描く", "えをかく", "E wo kaku", "Vẽ tranh");
        addV(lesson, "料理", "りょうり", "Ryouri", "Nấu ăn");
        addV(lesson, "習慣", "しゅうかん", "Shuukan", "Thói quen");
        addV(lesson, "朝", "あさ", "Asa", "Sáng");
        addV(lesson, "夜", "よる", "Yoru", "Tối");
        addV(lesson, "毎週", "まいしゅう", "Maishuu", "Mỗi tuần");
        addV(lesson, "週末", "しゅうまつ", "Shumatsu", "Cuối tuần");
        addG(lesson, "～のが好きです", "野球のが好きです。", "N4", "Sở thích");
        addG(lesson, "～ています", "毎日運動しています。", "N4", "Thói quen");
    }

    private void seedLesson2(Lesson lesson) {
        // Du lịch và phương tiện giao thông
        addV(lesson, "旅行", "りょこう", "Ryokou", "Du lịch");
        addV(lesson, "電車", "でんしゃ", "Densha", "Tàu điện");
        addV(lesson, "バス", "ばす", "Basu", "Xe buýt");
        addV(lesson, "飛行機", "ひこうき", "Hikouki", "Máy bay");
        addV(lesson, "車", "くるま", "Kuruma", "Ô tô");
        addV(lesson, "駅", "えき", "Eki", "Ga tàu");
        addV(lesson, "空港", "くうこう", "Kuukou", "Sân bay");
        addV(lesson, "切符", "きっぷ", "Kippu", "Vé");
        addV(lesson, "ホテル", "ほてる", "Hoteru", "Khách sạn");
        addV(lesson, "観光", "かんこう", "Kankou", "Du lịch");
        addV(lesson, "風景", "ふうけい", "Fuukei", "Phong cảnh");
        addV(lesson, "地図", "ちず", "Chizu", "Bản đồ");
        addV(lesson, "方向", "ほうこう", "Houkou", "Hướng");
        addV(lesson, "北", "きた", "Kita", "Bắc");
        addV(lesson, "南", "みなみ", "Minami", "Nam");
        addV(lesson, "東", "ひがし", "Higashi", "Đông");
        addV(lesson, "西", "にし", "Nishi", "Tây");
        addG(lesson, "～のに", "行くのに30分", "N4", "Mục đích");
        addG(lesson, "～ために", "旅行するために", "N4", "Mục đích");
    }

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
}
