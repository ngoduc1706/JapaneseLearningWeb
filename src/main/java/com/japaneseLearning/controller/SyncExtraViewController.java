package com.japaneseLearning.controller;

import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class SyncExtraViewController {

    private static final Set<String> KANJI_COMPOSE_PAGES = Set.of(
            "index",
            "match-test",
            "result",
            "test"
    );

    private static final Set<String> KANJI_RADICAL_PAGES = Set.of(
            "index",
            "create",
            "edit",
            "details",
            "delete"
    );

    private static final Set<String> ACCOUNT_UTIL_PAGES = Set.of(
            "check-user-id-storage",
            "local-storage-debug"
    );

    @GetMapping("/sync/kanji-compose/{page}")
    public String kanjiCompose(@PathVariable String page, Model model) {
        if (!KANJI_COMPOSE_PAGES.contains(page)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unsupported kanji compose page");
        }
        model.addAttribute("pageTitle", "Kanji Compose - " + page);
        return "kanji-compose-sync/" + page;
    }

    @GetMapping("/sync/kanji-radical/{page}")
    public String kanjiRadical(@PathVariable String page, Model model) {
        if (!KANJI_RADICAL_PAGES.contains(page)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unsupported kanji radical page");
        }
        model.addAttribute("pageTitle", "Kanji Radical - " + page);
        return "kanji-radical-sync/" + page;
    }

    @GetMapping("/sync/account-util/{page}")
    public String accountUtil(@PathVariable String page, Model model) {
        if (!ACCOUNT_UTIL_PAGES.contains(page)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unsupported account utility page");
        }
        model.addAttribute("pageTitle", "Account Utility - " + page);
        return "account-util-sync/" + page;
    }
}
