package com.japaneseLearning.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple voucher/coupon endpoint for the checkout flow.
 * Vouchers are stored in-memory for demo purposes.
 */
@RestController
@RequestMapping("/api/voucher")
public class VoucherController {

    /** Build-in demo voucher codes. Format: code → discount percent */
    private static final Map<String, Integer> VOUCHERS = new HashMap<>();
    static {
        VOUCHERS.put("DEMO50",    50);   // 50% off
        VOUCHERS.put("JLPT30",    30);   // 30% off
        VOUCHERS.put("WELCOME20", 20);   // 20% off
        VOUCHERS.put("STUDY10",   10);   // 10% off
        VOUCHERS.put("FREE100",  100);   // 100% off (fully free)
    }

    /**
     * Apply a voucher code to an amount.
     *
     * @param code   Voucher code (case-insensitive)
     * @param amount Original amount in VND (e.g. 299000)
     * @return JSON with success/error and new discounted price
     */
    @GetMapping("/apply")
    public ResponseEntity<Map<String, Object>> apply(
            @RequestParam String code,
            @RequestParam(defaultValue = "0") double amount) {

        Map<String, Object> res = new HashMap<>();
        String upper = code.trim().toUpperCase();

        Integer discountPct = VOUCHERS.get(upper);
        if (discountPct == null) {
            res.put("success", false);
            res.put("message", "Mã voucher không hợp lệ hoặc đã hết hạn 😔");
            return ResponseEntity.ok(res);
        }

        long discounted = Math.round(amount * (100 - discountPct) / 100.0);
        long saved      = Math.round(amount) - discounted;

        res.put("success",      true);
        res.put("data",         discounted);          // new price
        res.put("discount",     discountPct);         // percent
        res.put("saved",        saved);               // VND saved
        res.put("message",      "🎉 Giảm " + discountPct + "%! Tiết kiệm "
                + String.format("%,d", saved).replace(',', '.') + "đ");

        return ResponseEntity.ok(res);
    }
}
