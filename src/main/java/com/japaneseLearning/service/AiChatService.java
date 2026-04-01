package com.japaneseLearning.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiChatService {

    @Value("${external-api.gemini.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public AiChatService(ObjectMapper objectMapper) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = objectMapper;
    }

    public String generateReply(String userMessage) {
        // Use gemini-2.5-flash since Google completely restricted 2.0-flash on new free tiers
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> payload = new HashMap<>();

            List<Map<String, Object>> contents = new ArrayList<>();
            Map<String, Object> contentItem = new HashMap<>();
            
            List<Map<String, Object>> parts = new ArrayList<>();
            Map<String, Object> part = new HashMap<>();
            
            // System prompt prepended to user query for legacy support
            String prompt = "Bạn là giáo viên tiếng Nhật ảo (Sensei) của trang web học tiếng Nhật, bạn cực kỳ hiền lành và thương học viên.\n" +
                            "YÊU CẦU BẮT BUỘC:\n" +
                            "1. LUÔN LUÔN giao tiếp và giải thích bằng TIẾNG VIỆT 100% (chỉ dùng tiếng Nhật khi cần trích dẫn từ vựng/ngữ pháp đang học).\n" +
                            "2. Giải thích thật ngắn gọn, siêu dễ hiểu, theo phong cách gần gũi như một người thầy/cô trò chuyện trực tiếp với học viên xưng 'Sensei' và 'em'.\n" +
                            "3. Khi trích dẫn tiếng Nhật phải luôn luôn kèm theo Romaji và nghĩa tiếng Việt.\n\n" +
                            "Câu hỏi của học viên: " + userMessage;
            part.put("text", prompt);
            parts.add(part);

            contentItem.put("role", "user");
            contentItem.put("parts", parts);
            contents.add(contentItem);
            
            payload.put("contents", contents);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            String response = restTemplate.postForObject(url, entity, String.class);
            
            JsonNode rootNode = objectMapper.readTree(response);
            if (rootNode.has("candidates") && rootNode.get("candidates").isArray() && rootNode.get("candidates").size() > 0) {
                JsonNode candidate = rootNode.get("candidates").get(0);
                if (candidate.has("content") && candidate.get("content").has("parts")) {
                    return candidate.get("content").get("parts").get(0).get("text").asText();
                }
            }
            return "Xin lỗi, sensei đang gặp chút sự cố trong việc suy nghĩ. Em thử lại sau nhé!";
        } catch (Exception e) {
            System.err.println("[AI Chat Error] " + e.getMessage());
            return "Lỗi cấu hình mạng (Debug): " + e.getMessage() + ". Có vẻ API Key của bạn đã bị khóa hoặc hết hạn!";
        }
    }
}
