package com.japaneseLearning.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service to handle MoMo Payment API integration
 */
@Service
public class MomoService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MomoService.class);

    @Value("${external-api.momo.api-url}")
    private String apiUrl;

    @Value("${external-api.momo.secret-key}")
    private String secretKey;

    @Value("${external-api.momo.access-key}")
    private String accessKey;

    @Value("${external-api.momo.partner-code}")
    private String partnerCode;

    @Value("${external-api.momo.return-url}")
    private String returnUrl;

    @Value("${external-api.momo.notify-url}")
    private String notifyUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Create a payment URL by calling MoMo's Create Order API
     */
    public String createPaymentUrl(Long courseId, Double amount, String userId) {
        String orderId = UUID.randomUUID().toString();
        return createPaymentUrl(courseId, amount, userId, orderId);
    }

    /**
     * Create payment URL with provided orderId so callback can map back to local payment.
     */
    public String createPaymentUrl(Long courseId, Double amount, String userId, String orderId) {
        String requestId = UUID.randomUUID().toString();
        String orderInfo = "Thẻ học khóa học #" + courseId;
        // extraData used to pass metadata back to our system
        String extraData = "userId=" + userId + ";courseId=" + courseId;

        // Raw data format required for MoMo signature (Order of fields matters!)
        String rawHash = "accessKey=" + accessKey +
                        "&amount=" + amount.longValue() +
                        "&extraData=" + extraData +
                        "&ipnUrl=" + notifyUrl +
                        "&orderId=" + orderId +
                        "&orderInfo=" + orderInfo +
                        "&partnerCode=" + partnerCode +
                        "&redirectUrl=" + returnUrl +
                        "&requestId=" + requestId +
                        "&requestType=captureWallet";

        try {
            log.info("Generating MoMo signature for RawHash: {}", rawHash);
            String signature = hmacSha256(rawHash, secretKey);

            Map<String, Object> body = new HashMap<>();
            body.put("partnerCode", partnerCode);
            body.put("partnerName", "Japanese Learning Web");
            body.put("storeId", "JapaneseLearningStore");
            body.put("requestId", requestId);
            body.put("amount", amount.longValue());
            body.put("orderId", orderId);
            body.put("orderInfo", orderInfo);
            body.put("redirectUrl", returnUrl);
            body.put("ipnUrl", notifyUrl);
            body.put("lang", "vi");
            body.put("extraData", extraData);
            body.put("requestType", "captureWallet");
            body.put("signature", signature);

            log.info("Sending request to MoMo API: {}", apiUrl);
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(apiUrl, body, Map.class);
            
            if (response != null) {
                log.info("MoMo API Response: status={}, message={}", response.get("resultCode"), response.get("message"));
                if (response.containsKey("payUrl")) {
                    return (String) response.get("payUrl");
                }
            }
        } catch (Exception e) {
            log.error("Critical error creating MoMo payment URL", e);
        }
        return null;
    }

    /**
     * HMAC SHA256 signature generator
     */
    private String hmacSha256(String data, String key) throws Exception {
        byte[] byteKey = key.getBytes(StandardCharsets.UTF_8);
        final Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        hmacSha256.init(new SecretKeySpec(byteKey, "HmacSHA256"));
        byte[] macData = hmacSha256.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return toHexString(macData);
    }

    private String toHexString(byte[] bytes) {
        try (java.util.Formatter formatter = new java.util.Formatter()) {
            for (byte b : bytes) {
                formatter.format("%02x", b);
            }
            return formatter.toString();
        }
    }

    /**
     * JL-57: Verify MoMo callback signature.
     */
    public boolean verifyCallbackSignature(Map<String, String> params) {
        if (params == null || !params.containsKey("signature")) {
            return false;
        }

        String callbackSignature = params.get("signature");
        
        String rawData = 
                "accessKey=" + accessKey +
                "&amount=" + params.getOrDefault("amount", "") +
                "&extraData=" + params.getOrDefault("extraData", "") +
                "&message=" + params.getOrDefault("message", "") +
                "&orderId=" + params.getOrDefault("orderId", "") +
                "&orderInfo=" + params.getOrDefault("orderInfo", "") +
                "&orderType=" + params.getOrDefault("orderType", "") +
                "&partnerCode=" + params.getOrDefault("partnerCode", "") +
                "&payType=" + params.getOrDefault("payType", "") +
                "&requestId=" + params.getOrDefault("requestId", "") +
                "&responseTime=" + params.getOrDefault("responseTime", "") +
                "&resultCode=" + params.getOrDefault("resultCode", "") +
                "&transId=" + params.getOrDefault("transId", "");

        try {
            String expected = hmacSha256(rawData, secretKey);
            if (!expected.equalsIgnoreCase(callbackSignature)) {
                log.warn("Invalid MoMo signature: Expected={}, Actual={}, Data={}", expected, callbackSignature, rawData);
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error("Failed to verify MoMo callback signature", e);
            return false;
        }
    }

    /**
     * Parse extraData in format: userId=abc;courseId=3
     */
    public Map<String, String> parseExtraData(String extraData) {
        Map<String, String> result = new HashMap<>();
        if (extraData == null || extraData.isBlank()) {
            return result;
        }

        String[] pairs = extraData.split(";");
        for (String pair : pairs) {
            if (pair == null || pair.isBlank() || !pair.contains("=")) {
                continue;
            }
            String[] kv = pair.split("=", 2);
            if (kv.length == 2 && kv[0] != null && kv[1] != null) {
                result.put(kv[0], kv[1]);
            }
        }
        return result;
    }

    private String buildRawSignatureData(Map<String, String> data) {
        ArrayList<Map.Entry<String, String>> entries = new ArrayList<>(data.entrySet());
        entries.sort(Comparator.comparing(Map.Entry::getKey));

        StringBuilder raw = new StringBuilder();
        for (Map.Entry<String, String> entry : entries) {
            if (entry.getValue() == null) {
                continue;
            }
            if (raw.length() > 0) {
                raw.append("&");
            }
            raw.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return raw.toString();
    }
}
