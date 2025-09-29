package com.pichincha.accounts.infrastructure.adapter.pdf;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PdfGeneratorAdapterTest {

    @Test
    void shouldThrowRuntimeExceptionWhenHtmlIsNullOnGeneratePdf() {
        PdfGeneratorAdapter adapter = new PdfGeneratorAdapter();
        assertThatThrownBy(() -> adapter.generatePdf(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error generando PDF");
    }

    @Test
    void shouldThrowRuntimeExceptionWhenHtmlIsNullOnGenerateBase64Pdf() {
        PdfGeneratorAdapter adapter = new PdfGeneratorAdapter();
        assertThatThrownBy(() -> adapter.generateBase64Pdf(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error generando PDF Base64");
    }
}
