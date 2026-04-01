package com.japaneseLearning.controller;

import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class SyncSharedViewController {

    private static final Set<String> SHARED_PAGES = Set.of(
            "asr-widget",
            "layout-preview",
            "validation-scripts",
            "error"
    );

    private static final Set<String> LESSONS_PAGES = Set.of(
            "grammar-partial",
            "kanji-partial",
            "vocab-partial",
            "course-selection"
    );

    @GetMapping("/sync/shared/{page}")
    public String shared(@PathVariable String page, Model model) {
        if (!SHARED_PAGES.contains(page)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unsupported shared page");
        }
        model.addAttribute("pageTitle", "Shared - " + page);
        return "shared-sync/" + page;
    }

    @GetMapping("/sync/lessons/{page}")
    public String lessons(@PathVariable String page, Model model) {
        if (!LESSONS_PAGES.contains(page)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unsupported lessons sync page");
        }
        model.addAttribute("pageTitle", "Lessons - " + page);
        return "lessons-sync/" + page;
    }
}
