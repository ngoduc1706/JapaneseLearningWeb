package com.japaneseLearning.controller;

import com.japaneseLearning.repository.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminViewController {

    private final ApplicationUserRepository userRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;

    public AdminViewController(ApplicationUserRepository userRepository,
                               CourseRepository courseRepository,
                               LessonRepository lessonRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
    }

    /** Admin dashboard — shows stats & quick action cards */
    @GetMapping({"", "/"})
    public String dashboard(Model model) {
        model.addAttribute("totalUsers",   userRepository.count());
        model.addAttribute("totalCourses", courseRepository.count());
        model.addAttribute("totalLessons", lessonRepository.count());
        model.addAttribute("allCourses",   courseRepository.findAll());
        model.addAttribute("allUsers",     userRepository.findAll());
        model.addAttribute("pageTitle",    "Admin Panel");
        return "admin/dashboard";
    }

    /** Toggle admin flag for a user */
    @PostMapping("/users/{id}/toggle-admin")
    public String toggleAdmin(@PathVariable String id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setIsAdmin(!user.getIsAdmin());
            userRepository.save(user);
        });
        return "redirect:/admin";
    }

    /** Delete a course */
    @PostMapping("/courses/{id}/delete")
    public String deleteCourse(@PathVariable Long id) {
        courseRepository.deleteById(id);
        return "redirect:/admin";
    }
}
