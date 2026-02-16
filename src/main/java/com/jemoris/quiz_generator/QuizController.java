package com.jemoris.quiz_generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    @Autowired
    private PdfService pdfService;

    @Autowired
    private QuizService quizService; // <--- NEW: The AI Service

    @PostMapping("/generate")
    public String generateQuiz(@RequestParam("file") MultipartFile file) {

        // 1. Extract Text from PDF
        String extractedText = pdfService.extractText(file);
        System.out.println("Text extracted. Sending to AI...");

        // 2. Send Text to Gemini AI
        String aiResponse = quizService.createQuizFromText(extractedText);

        // 3. Return the AI's answer
        return aiResponse;
    }
}