package com.pichincha.accounts.infrastructure.adapter.rest;

import com.pichincha.accounts.application.port.input.ReportInputPort;
import com.pichincha.accounts.domain.Report;
import com.pichincha.accounts.domain.exception.ClientNotFoundException;
import com.pichincha.accounts.infrastructure.mapper.ReportDtoMapper;
import com.pichincha.infrastructure.adapter.rest.dto.ReportDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:4200", "http://127.0.0.1:4200"})
public class ReportController {

    private final ReportInputPort reportInputPort;
    private final ReportDtoMapper reportDtoMapper;

    @GetMapping
    public ResponseEntity<ReportDto> generateReport(
            @RequestParam UUID clienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        log.info("Generating report for client ID: {} from {} to {}", clienteId, fechaInicio, fechaFin);

        try {
            Report report = reportInputPort.generateReport(clienteId, fechaInicio, fechaFin);
            return ResponseEntity.ok(reportDtoMapper.toDto(report));
        } catch (ClientNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            log.error("Error generating report: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> generateReportPdf(
            @RequestParam UUID clienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        log.info("Generating PDF report for client ID: {} from {} to {}", clienteId, fechaInicio, fechaFin);

        try {
            byte[] pdfBytes = reportInputPort.generateReportPdf(clienteId, fechaInicio, fechaFin);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "reporte_" + clienteId + ".pdf");

        return ResponseEntity.ok()
            .headers(headers)
            .body(pdfBytes);

        } catch (ClientNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            log.error("Error generating PDF report: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/pdf/base64")
    public ResponseEntity<Map<String, String>> generateReportPdfBase64(
            @RequestParam UUID clienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        log.info("Generating PDF Base64 report for client ID: {} from {} to {}", clienteId, fechaInicio, fechaFin);

        try {
            String base64Pdf = reportInputPort.generateReportPdfBase64(clienteId, fechaInicio, fechaFin);

            Map<String, String> response = new HashMap<>();
            response.put("pdfBase64", base64Pdf);
            response.put("filename", "reporte_" + clienteId + ".pdf");

            return ResponseEntity.ok(response);

        } catch (ClientNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            log.error("Error generating PDF Base64 report: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}