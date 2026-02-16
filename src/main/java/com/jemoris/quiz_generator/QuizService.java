package com.jemoris.quiz_generator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

@Service
public class QuizService {

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.api.key}")
    private String apiKey;

    public String createQuizFromText(String content) {
        RestTemplate restTemplate = new RestTemplate();

        // THE "HARDCORE" PROMPT
        String prompt = "Act as a Senior University Examiner. Analyze this text and generate 20 'Exam-Level' Multiple Choice Questions. " +
                "CRITICAL INSTRUCTIONS: " +
                "1. AVOID simple definitions (e.g., 'What is IP?'). " +
                "2. FOCUS on 'Application' and 'Analysis' levels: " +
                "   - Code Debugging: Give a snippet and ask what's wrong. " +
                "   - Scenarios: 'A client fails to connect with Error X. What is the cause?' " +
                "   - Trade-offs: 'Why use Function A over Function B in this specific case?' " +
                "3. DISTRACTORS: The wrong answers must be technically plausible to test deep understanding. " +
                "4. EXPLANATIONS: Must be detailed technical breakdowns. " +
                "Return ONLY a JSON array with keys: 'question', 'options' (4 strings), 'correctAnswer' (0-3), 'explanation'. " +
                "Content: " + content;

        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);

        Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("parts", List.of(part));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(contentMap));

        String fullUrl = apiUrl + apiKey;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            Map response = restTemplate.postForObject(fullUrl, request, Map.class);

            List candidates = (List) response.get("candidates");
            Map firstCandidate = (Map) candidates.get(0);
            Map contentObj = (Map) firstCandidate.get("content");
            List parts = (List) contentObj.get("parts");
            String rawJson = (String) ((Map)parts.get(0)).get("text");

            return rawJson.replace("```json", "").replace("```", "").trim();

        } catch (Exception e) {
            System.out.println("AI Error: " + e.getMessage());
            return "[]";
        }
    }
}