package com.pichincha.accounts.application.port.output;

public interface PdfGeneratorPort {
    byte[] generatePdf(String htmlContent);
    String generateBase64Pdf(String htmlContent);
}