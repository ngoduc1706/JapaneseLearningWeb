package com.japaneseLearning.controller;

import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class LegacyCrudViewController {

    private static final Set<String> SUPPORTED_MODULES = Set.of(
            "courses",
            "lessons",
            "quizzes",
            "kanjis",
            "vocabularies",
            "categories",
            "grammars"
    );

    private static final Set<String> SUPPORTED_PAGES = Set.of(
            "index",
            "create",
            "edit",
            "delete"
    );

    @GetMapping("/sync/{module}/{page}")
    public String syncCrudPages(@PathVariable String module, @PathVariable String page, Model model) {
        validate(module, page);
        model.addAttribute("pageTitle", toTitle(module, page));
        model.addAttribute("module", module);
        model.addAttribute("page", page);
        model.addAttribute("apiBase", "/api/" + module);
        return module + "/" + page;
    }

    private void validate(String module, String page) {
        if (!SUPPORTED_MODULES.contains(module) || !SUPPORTED_PAGES.contains(page)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unsupported sync page");
        }
    }

    private String toTitle(String module, String page) {
        String m = module.substring(0, 1).toUpperCase() + module.substring(1);
        String p = page.substring(0, 1).toUpperCase() + page.substring(1);
        return m + " - " + p;
    }
}
