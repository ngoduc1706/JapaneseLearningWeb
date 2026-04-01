package com.japaneseLearning.controller;

import com.japaneseLearning.service.AiChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatApiController {

    @Autowired
    private AiChatService aiChatService;

    @PostMapping
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> payload) {
        String userMessage = payload.get("message");
        if (userMessage == null || userMessage.trim().isEmpty()) {
            Map<String, String> err = new HashMap<>();
            err.put("reply", "Lỗi: Tin nhắn trống!");
            return ResponseEntity.badRequest().body(err);
        }

        // Call the AI Service to generate a reply
        String reply = aiChatService.generateReply(userMessage);

        Map<String, String> response = new HashMap<>();
        response.put("reply", reply);

        return ResponseEntity.ok(response);
    }
}
