package com.pichincha.accounts.application.service;

import com.pichincha.accounts.application.port.input.ReportUseCase;
import com.pichincha.accounts.application.port.output.AccountRepository;
import com.pichincha.accounts.application.port.output.ClientRepository;
import com.pichincha.accounts.application.port.output.MovementRepository;
import com.pichincha.accounts.application.port.output.PdfGeneratorPort;
import com.pichincha.accounts.domain.Account;
import com.pichincha.accounts.domain.Client;
import com.pichincha.accounts.domain.Movement;
import com.pichincha.accounts.domain.Report;
import com.pichincha.accounts.domain.enums.MovementType;
import com.pichincha.accounts.domain.exception.ClientNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReportService implements ReportUseCase {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final MovementRepository movementRepository;
    private final PdfGeneratorPort pdfGeneratorPort;

    @Override
    public Report generateReport(UUID clientId, LocalDate startDate, LocalDate endDate) {
        log.info("Generating report for client ID: {} from {} to {}", clientId, startDate, endDate);

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException("Cliente no encontrado con ID: " + clientId));

        List<Account> accounts = accountRepository.findByClientId(clientId);

        List<Report.AccountStatement> accountStatements = new ArrayList<>();
        BigDecimal totalCredits = BigDecimal.ZERO;
        BigDecimal totalDebits = BigDecimal.ZERO;
        BigDecimal totalBalance = BigDecimal.ZERO;

        for (Account account : accounts) {
            List<Movement> movements = movementRepository
                    .findByAccountIdAndDateRange(account.getId(), startDate, endDate);

            BigDecimal accountCredits = BigDecimal.ZERO;
            BigDecimal accountDebits = BigDecimal.ZERO;

            for (Movement movement : movements) {
                if (movement.getValue().compareTo(BigDecimal.ZERO) > 0) {
                    accountCredits = accountCredits.add(movement.getValue());
                } else {
                    accountDebits = accountDebits.add(movement.getValue().abs());
                }
            }

            Report.AccountStatement statement = Report.AccountStatement.builder()
                    .account(account)
                    .movements(movements)
                    .accountTotalCredits(accountCredits)
                    .accountTotalDebits(accountDebits)
                    .finalBalance(account.getCurrentBalance())
                    .build();

            accountStatements.add(statement);
            totalCredits = totalCredits.add(accountCredits);
            totalDebits = totalDebits.add(accountDebits);
            totalBalance = totalBalance.add(account.getCurrentBalance());
        }

        Report report = Report.builder()
                .startDate(startDate)
                .endDate(endDate)
                .client(client)
                .accountStatements(accountStatements)
                .totalCredits(totalCredits)
                .totalDebits(totalDebits)
                .totalBalance(totalBalance)
                .build();

        log.info("Report generated successfully for client: {}", client.getName());
        return report;
    }

    @Override
    public String generateReportPdf(UUID clientId, LocalDate startDate, LocalDate endDate) {
        log.info("Generating PDF report for client ID: {}", clientId);

        Report report = generateReport(clientId, startDate, endDate);
        String htmlContent = generateHtmlContent(report);

        byte[] pdfBytes = pdfGeneratorPort.generatePdf(htmlContent);
        return new String(pdfBytes);
    }

    @Override
    public String generateReportPdfBase64(UUID clientId, LocalDate startDate, LocalDate endDate) {
        log.info("Generating PDF Base64 report for client ID: {}", clientId);

        Report report = generateReport(clientId, startDate, endDate);
        String htmlContent = generateHtmlContent(report);

        return pdfGeneratorPort.generateBase64Pdf(htmlContent);
    }

    private String generateHtmlContent(Report report) {
        StringBuilder html = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        html.append("<!DOCTYPE html>");
        html.append("<html><head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; margin: 20px; }");
        html.append("h1 { text-align: center; color: #2c3e50; }");
        html.append("h2 { color: #34495e; border-bottom: 2px solid #3498db; padding-bottom: 5px; }");
        html.append("table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }");
        html.append("th, td { border: 1px solid #bdc3c7; padding: 8px; text-align: left; }");
        html.append("th { background-color: #3498db; color: white; }");
        html.append(".client-info { background-color: #ecf0f1; padding: 15px; margin-bottom: 20px; }");
        html.append(".summary { background-color: #e8f5e8; padding: 15px; margin-top: 20px; }");
        html.append(".credit { color: #27ae60; font-weight: bold; }");
        html.append(".debit { color: #e74c3c; font-weight: bold; }");
        html.append("</style>");
        html.append("</head><body>");

        html.append("<h1>Estado de Cuenta</h1>");

        html.append("<div class='client-info'>");
        html.append("<h2>Información del Cliente</h2>");
        html.append("<p><strong>Nombre:</strong> ").append(report.getClient().getName()).append("</p>");
        html.append("<p><strong>Identificación:</strong> ").append(report.getClient().getIdentification()).append("</p>");
        html.append("<p><strong>Período:</strong> ").append(report.getStartDate().format(formatter))
               .append(" - ").append(report.getEndDate().format(formatter)).append("</p>");
        html.append("</div>");

        for (Report.AccountStatement statement : report.getAccountStatements()) {
            html.append("<h2>Cuenta: ").append(statement.getAccount().getAccountNumber()).append("</h2>");
            html.append("<p><strong>Tipo:</strong> ").append(statement.getAccount().getType()).append("</p>");
            html.append("<p><strong>Estado:</strong> ").append(statement.getAccount().getState() ? "Activa" : "Inactiva").append("</p>");
            html.append("<p><strong>Saldo Actual:</strong> $").append(statement.getFinalBalance()).append("</p>");
            
            if (!statement.getMovements().isEmpty()) {
                html.append("<table>");
                html.append("<tr>");
                html.append("<th>Fecha</th>");
                html.append("<th>Tipo Movimiento</th>");
                html.append("<th>Valor</th>");
                html.append("<th>Saldo</th>");
                html.append("</tr>");
                
                for (Movement movement : statement.getMovements()) {
                    html.append("<tr>");
                    html.append("<td>").append(movement.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("</td>");
                    html.append("<td>").append(movement.getMovementType().getDescription()).append("</td>");
                    
                    String valueClass = movement.getValue().compareTo(BigDecimal.ZERO) > 0 ? "credit" : "debit";
                    html.append("<td class='").append(valueClass).append("'>$").append(movement.getValue()).append("</td>");
                    html.append("<td>$").append(movement.getBalance()).append("</td>");
                    html.append("</tr>");
                }
                
                html.append("</table>");

                html.append("<p><strong>Total Créditos:</strong> <span class='credit'>$").append(statement.getAccountTotalCredits()).append("</span></p>");
                html.append("<p><strong>Total Débitos:</strong> <span class='debit'>$").append(statement.getAccountTotalDebits()).append("</span></p>");
            } else {
                html.append("<p>No hay movimientos en el período seleccionado.</p>");
            }
        }

        html.append("<div class='summary'>");
        html.append("<h2>Resumen General</h2>");
        html.append("<p><strong>Total Créditos:</strong> <span class='credit'>$").append(report.getTotalCredits()).append("</span></p>");
        html.append("<p><strong>Total Débitos:</strong> <span class='debit'>$").append(report.getTotalDebits()).append("</span></p>");
        html.append("<p><strong>Saldo Total:</strong> $").append(report.getTotalBalance()).append("</p>");
        html.append("</div>");

        html.append("</body></html>");

        return html.toString();
    }
}
