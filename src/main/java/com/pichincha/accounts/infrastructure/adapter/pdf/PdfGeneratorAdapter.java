package com.pichincha.accounts.infrastructure.adapter.pdf;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.pichincha.accounts.application.port.output.PdfGeneratorPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Component
@Slf4j
public class PdfGeneratorAdapter implements PdfGeneratorPort {

    @Override
    public byte[] generatePdf(String htmlContent) {
        try {
            log.debug("Generating PDF from HTML content");
            
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.useFastMode();
                // base URI null can cause resource resolution issues; provide empty string
                builder.withHtmlContent(htmlContent, "");
                builder.toStream(outputStream);
                builder.run();
                byte[] pdfBytes = outputStream.toByteArray();
                log.debug("PDF generated successfully, size: {} bytes", pdfBytes.length);
                return pdfBytes;
            }
        } catch (Exception e) {
            log.error("Error generating PDF", e);
            throw new RuntimeException("Error generando PDF: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateBase64Pdf(String htmlContent) {
        try {
            log.debug("Generating Base64 PDF from HTML content");
            
            byte[] pdfBytes = generatePdf(htmlContent);
            String base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);
            
            log.debug("Base64 PDF generated successfully, length: {}", base64Pdf.length());
            return base64Pdf;
        } catch (Exception e) {
            log.error("Error generating Base64 PDF", e);
            throw new RuntimeException("Error generando PDF Base64: " + e.getMessage(), e);
        }
    }
}