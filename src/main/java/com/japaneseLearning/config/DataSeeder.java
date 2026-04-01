package com.japaneseLearning.config;

import com.japaneseLearning.entity.Category;
import com.japaneseLearning.entity.Course;
import com.japaneseLearning.entity.Lesson;
import com.japaneseLearning.repository.CategoryRepository;
import com.japaneseLearning.repository.CourseRepository;
import com.japaneseLearning.repository.LessonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    CommandLineRunner seedInitialData(
            CategoryRepository categoryRepository,
            CourseRepository courseRepository,
            LessonRepository lessonRepository) {
        return args -> {
            // Check if specific starter course exists instead of any course
            boolean starterExists = courseRepository.findAll().stream()
                .anyMatch(c -> c.getTitle().equals("Japanese N5 Starter"));
            
            if (starterExists) {
                return;
            }

            Category n5Category = categoryRepository.findByNameIgnoreCase("JLPT N5");
            if (n5Category == null) {
                n5Category = new Category();
                n5Category.setName("JLPT N5");
                n5Category.setDescription("Beginner level Japanese for absolute starters.");
                n5Category = categoryRepository.save(n5Category);
            }

            Course starterCourse = new Course();
            starterCourse.setTitle("Japanese N5 Starter");
            starterCourse.setDescription("Start learning hiragana, basic greetings, and simple grammar.");
            starterCourse.setIsFree(true);
            starterCourse.setPrice(0.0);
            starterCourse.setCategory(n5Category);
            starterCourse = courseRepository.save(starterCourse);

            Lesson lesson1 = new Lesson();
            lesson1.setCourse(starterCourse);
            lesson1.setTitle("Lesson 1: Greetings and Self-Introduction");
            lesson1.setContent("Learn basic greetings like ohayo, konnichiwa, konbanwa, and how to introduce yourself.");
            lesson1.setOrderInCourse(1);

            Lesson lesson2 = new Lesson();
            lesson2.setCourse(starterCourse);
            lesson2.setTitle("Lesson 2: Hiragana Basics");
            lesson2.setContent("Learn the first set of hiragana characters and pronunciation practice.");
            lesson2.setOrderInCourse(2);

            lessonRepository.save(lesson1);
            lessonRepository.save(lesson2);

            log.info("Seeded demo data: 1 category, 1 course, 2 lessons.");
        };
    }
}
