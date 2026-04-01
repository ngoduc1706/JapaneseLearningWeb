package com.japaneseLearning.controller;

import java.util.Set;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Controller
public class ManageViewController {

    private static final Set<String> SUPPORTED_ENTITIES = Set.of(
            "courses",
            "lessons",
            "quizzes",
            "kanjis",
            "vocabularies",
            "categories",
            "grammars"
    );

    @GetMapping("/manage")
    public String manageHome(Model model) {
        model.addAttribute("pageTitle", "Manage CRUD");
        return "manage/index";
    }

    @GetMapping("/manage/{entity}")
    public String manageList(@PathVariable String entity, Model model) {
        validateEntity(entity);
        model.addAttribute("pageTitle", buildTitle(entity, "List"));
        model.addAttribute("entity", entity);
        model.addAttribute("apiBase", "/api/" + entity);
        return "manage/list";
    }

    @GetMapping("/manage/{entity}/create")
    public String manageCreate(@PathVariable String entity, Model model) {
        validateEntity(entity);
        model.addAttribute("pageTitle", buildTitle(entity, "Create"));
        model.addAttribute("entity", entity);
        model.addAttribute("apiBase", "/api/" + entity);
        return "manage/create";
    }

    @GetMapping("/manage/{entity}/edit")
    public String manageEdit(@PathVariable String entity, Model model) {
        validateEntity(entity);
        model.addAttribute("pageTitle", buildTitle(entity, "Edit"));
        model.addAttribute("entity", entity);
        model.addAttribute("apiBase", "/api/" + entity);
        return "manage/edit";
    }

    @GetMapping("/manage/{entity}/delete")
    public String manageDelete(@PathVariable String entity, Model model) {
        validateEntity(entity);
        model.addAttribute("pageTitle", buildTitle(entity, "Delete"));
        model.addAttribute("entity", entity);
        model.addAttribute("apiBase", "/api/" + entity);
        return "manage/delete";
    }

    private void validateEntity(String entity) {
        if (!SUPPORTED_ENTITIES.contains(entity)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unsupported entity: " + entity);
        }
    }

    private String buildTitle(String entity, String action) {
        String normalized = entity.substring(0, 1).toUpperCase() + entity.substring(1);
        return action + " " + normalized;
    }
}
