package com.pichincha.accounts.infrastructure.adapter.pdf;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.pichincha.accounts.application.port.output.PdfGeneratorPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Component
public class PdfGeneratorAdapter implements PdfGeneratorPort {

    @Override
    public byte[] generatePdf(String htmlContent) {
        try {

            
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.useFastMode();

                builder.withHtmlContent(htmlContent, "");
                builder.toStream(outputStream);
                builder.run();
                byte[] pdfBytes = outputStream.toByteArray();

                return pdfBytes;
            }
        } catch (Exception e) {

            throw new RuntimeException("Error generando PDF: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateBase64Pdf(String htmlContent) {
        try {
            
            byte[] pdfBytes = generatePdf(htmlContent);
            String base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);

            return base64Pdf;
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF Base64: " + e.getMessage(), e);
        }
    }
}