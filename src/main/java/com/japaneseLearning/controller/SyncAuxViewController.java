package com.japaneseLearning.controller;

import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class SyncAuxViewController {

    private static final Set<String> ACCOUNT_PAGES = Set.of(
            "history",
            "payment",
            "register-course",
            "reset-password"
    );

    private static final Set<String> ADMIN_PAGES = Set.of(
            "create-user",
            "edit-user",
            "user-management"
    );

    private static final Set<String> PAYMENT_PAGES = Set.of(
            "payment-callback",
            "payment-fail"
    );

    @GetMapping("/sync/account/{page}")
    public String accountPage(@PathVariable String page, Model model) {
        if (!ACCOUNT_PAGES.contains(page)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unsupported account page");
        }
        model.addAttribute("pageTitle", "Account - " + page);
        return "account-sync/" + page;
    }

    @GetMapping("/sync/admin/{page}")
    public String adminPage(@PathVariable String page, Model model) {
        if (!ADMIN_PAGES.contains(page)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unsupported admin page");
        }
        model.addAttribute("pageTitle", "Admin - " + page);
        return "admin-sync/" + page;
    }

    @GetMapping("/sync/payment/{page}")
    public String paymentPage(@PathVariable String page, Model model) {
        if (!PAYMENT_PAGES.contains(page)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unsupported payment page");
        }
        model.addAttribute("pageTitle", "Payment - " + page);
        return "payment-sync/" + page;
    }
}
