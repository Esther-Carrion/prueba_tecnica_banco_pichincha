package com.pichincha.accounts.application.port.input;

import com.pichincha.accounts.domain.Report;

import java.time.LocalDate;
import java.util.UUID;

public interface ReportInputPort {
    Report generateReport(UUID clientId, LocalDate startDate, LocalDate endDate);
    byte[] generateReportPdf(UUID clientId, LocalDate startDate, LocalDate endDate);
    String generateReportPdfBase64(UUID clientId, LocalDate startDate, LocalDate endDate);
}