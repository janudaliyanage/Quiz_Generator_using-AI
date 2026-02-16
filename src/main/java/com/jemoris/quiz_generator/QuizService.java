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

        // The "Final Form" Prompt: Mixed difficulty, full coverage, deep explanations
        String prompt = "Step 1: Perform a deep analysis of the provided PDF content. " +
                "Step 2: Generate a QUIZ that covers ALL the content in the PDF. " +
                "Step 3: The Quizes must be MIXED (from very hard technical quizzes to easy conceptual quizzes). " +
                "Ensure you include questions on datatypes, logic flows, and specific code functions. " +
                "For every answer, provide a high-quality explanation. " +
                "Return ONLY a JSON array with keys: 'question', 'options' (array of 4 strings), 'correctAnswer' (integer 0-3), 'explanation'. " +
                "Content to analyze: " + content;

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

            // Safe digging to get the text
            List candidates = (List) response.get("candidates");
            Map firstCandidate = (Map) candidates.get(0);
            Map contentObj = (Map) firstCandidate.get("content");
            List parts = (List) contentObj.get("parts");
            String rawJson = (String) ((Map)parts.get(0)).get("text");

            // Clean up Markdown if the AI adds it
            return rawJson.replace("```json", "").replace("```", "").trim();

        } catch (Exception e) {
            System.out.println("AI Error: " + e.getMessage());
            return "[]";
        }
    }
}