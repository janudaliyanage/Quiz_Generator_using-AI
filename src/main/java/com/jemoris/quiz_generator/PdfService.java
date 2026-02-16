package com.jemoris.quiz_generator;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
public class PdfService {

    public String extractText(MultipartFile file) {
        // This tries to open the file. If it fails, it goes to "catch"
        try (PDDocument document = PDDocument.load(file.getInputStream())) {

            PDFTextStripper stripper = new PDFTextStripper();

            // Limit to first 5 pages to save speed/money
            stripper.setEndPage(5);

            String text = stripper.getText(document);
            return text.trim(); // .trim() removes extra empty spaces

        } catch (IOException e) {
            throw new RuntimeException("Error reading PDF file", e);
        }
    }
}