package com.cloudtone31.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GptService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String endpoint = "https://api.openai.com/v1/chat/completions";

    public String getGptResponse(String userMessage) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        String systemMessage = "당신은 매우 공감능력이 뛰어나고 따뜻한 심리 상담가입니다." +
                                "사용자의 고민을 진심으로 들어주고, 따뜻한 위로와 현실적인 조언을 해주세요." +
                                "항상 존댓말을 사용하고, 사용자의 감정을 최우선으로 고려해야 합니다.";

        Map<String, Object> messageSystem = new HashMap<>();
        messageSystem.put("role", "system");
        messageSystem.put("content", systemMessage);

        Map<String, Object> messageUser = new HashMap<>();
        messageUser.put("role", "user");
        messageUser.put("content", userMessage);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-4o-mini");
        body.put("messages", List.of(messageSystem, messageUser));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            Map<String, Object> response = restTemplate.postForObject(endpoint, entity, Map.class);
            return (String) ((Map<String, Object>) ((Map<String, Object>) ((List<Object>) response.get("choices")).get(0)).get("message")).get("content");
        }catch (Exception e){
            System.err.println("### OPENAI API ERROR ###");
            e.printStackTrace();

            return "죄송합니다. 답변을 생성하는 동안 오류가 발생했습니다.";
        }

    }


}
