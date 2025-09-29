package com.pichincha.accounts.infrastructure.adapter.rest;

import com.pichincha.accounts.application.port.input.ReportInputPort;
import com.pichincha.accounts.domain.Report;
import com.pichincha.accounts.domain.exception.ClientNotFoundException;
import com.pichincha.accounts.infrastructure.mapper.ReportDtoMapper;
import com.pichincha.infrastructure.adapter.rest.dto.ReportDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    @Mock
    private ReportInputPort reportInputPort;
    @Mock
    private ReportDtoMapper reportDtoMapper;

    @InjectMocks
    private ReportController controller;

    @Test
    void shouldReturnOkWhenGenerateReportSucceeds() {
        UUID clientId = UUID.randomUUID();
        LocalDate start = LocalDate.of(2024,1,1);
        LocalDate end = LocalDate.of(2024,12,31);
        Report report = new Report();
        ReportDto dto = new ReportDto();
        when(reportInputPort.generateReport(clientId, start, end)).thenReturn(report);
        when(reportDtoMapper.toDto(report)).thenReturn(dto);

        ResponseEntity<ReportDto> response = controller.generateReport(clientId, start, end);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(dto);
    }

    @Test
    void shouldReturnNotFoundWhenGenerateReportClientNotFound() {
        UUID clientId = UUID.randomUUID();
        LocalDate start = LocalDate.of(2024,1,1);
        LocalDate end = LocalDate.of(2024,12,31);
        when(reportInputPort.generateReport(clientId, start, end)).thenThrow(new ClientNotFoundException("not found"));

        ResponseEntity<ReportDto> response = controller.generateReport(clientId, start, end);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturnOkWithPdfBytesWhenGenerateReportPdfSucceeds() {
        UUID clientId = UUID.randomUUID();
        LocalDate start = LocalDate.of(2024,1,1);
        LocalDate end = LocalDate.of(2024,12,31);
        when(reportInputPort.generateReportPdf(clientId, start, end)).thenReturn("PDF_CONTENT".getBytes());

        ResponseEntity<byte[]> response = controller.generateReportPdf(clientId, start, end);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldReturnInternalServerErrorWhenGenerateReportPdfThrows() {
        UUID clientId = UUID.randomUUID();
        LocalDate start = LocalDate.of(2024,1,1);
        LocalDate end = LocalDate.of(2024,12,31);
    when(reportInputPort.generateReportPdf(clientId, start, end)).thenThrow(new RuntimeException("boom"));

        ResponseEntity<byte[]> response = controller.generateReportPdf(clientId, start, end);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void shouldReturnOkWithBase64WhenGenerateReportPdfBase64Succeeds() {
        UUID clientId = UUID.randomUUID();
        LocalDate start = LocalDate.of(2024,1,1);
        LocalDate end = LocalDate.of(2024,12,31);
        when(reportInputPort.generateReportPdfBase64(clientId, start, end)).thenReturn("BASE64");

        ResponseEntity<Map<String, String>> response = controller.generateReportPdfBase64(clientId, start, end);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("pdfBase64", "BASE64");
    }

    @Test
    void shouldReturnNotFoundWhenGenerateReportPdfBase64ClientNotFound() {
        UUID clientId = UUID.randomUUID();
        LocalDate start = LocalDate.of(2024,1,1);
        LocalDate end = LocalDate.of(2024,12,31);
        when(reportInputPort.generateReportPdfBase64(clientId, start, end)).thenThrow(new ClientNotFoundException("not found"));

        ResponseEntity<Map<String, String>> response = controller.generateReportPdfBase64(clientId, start, end);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturnInternalServerErrorWhenGenerateReportPdfBase64Throws() {
        UUID clientId = UUID.randomUUID();
        LocalDate start = LocalDate.of(2024,1,1);
        LocalDate end = LocalDate.of(2024,12,31);
        when(reportInputPort.generateReportPdfBase64(clientId, start, end)).thenThrow(new RuntimeException("boom"));

        ResponseEntity<Map<String, String>> response = controller.generateReportPdfBase64(clientId, start, end);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
