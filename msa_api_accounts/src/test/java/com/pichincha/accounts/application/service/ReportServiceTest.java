package com.pichincha.accounts.application.service;

import com.pichincha.accounts.application.port.output.AccountRepository;
import com.pichincha.accounts.application.port.output.ClientRepository;
import com.pichincha.accounts.application.port.output.MovementRepository;
import com.pichincha.accounts.application.port.output.PdfGeneratorPort;
import com.pichincha.accounts.domain.Account;
import com.pichincha.accounts.domain.Client;
import com.pichincha.accounts.domain.Movement;
import com.pichincha.accounts.domain.Report;
import com.pichincha.accounts.domain.enums.AccountType;
import com.pichincha.accounts.domain.enums.Gender;
import com.pichincha.accounts.domain.enums.MovementType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock private ClientRepository clientRepository;
    @Mock private AccountRepository accountRepository;
    @Mock private MovementRepository movementRepository;
    @Mock private PdfGeneratorPort pdfGeneratorPort;

    @InjectMocks private ReportService reportService;

    @Test
    void generateReport_aggregatesTotalsCorrectly() {
        UUID clientId = UUID.randomUUID();
        Client client = Client.builder()
                .id(clientId)
                .name("Juan")
                .identification("0102030405")
                .gender(Gender.MASCULINO)
                .state(true)
                .build();

        Account acc = Account.builder()
                .id(UUID.randomUUID())
                .clientId(clientId)
                .accountNumber("1234567890")
                .type(AccountType.AHORROS)
                .currentBalance(new BigDecimal("120.00"))
                .state(true)
                .build();

        Movement m1 = Movement.builder()
                .id(UUID.randomUUID())
                .accountId(acc.getId())
                .date(LocalDateTime.now())
                .movementType(MovementType.DEPOSITO)
                .value(new BigDecimal("50.00"))
                .balance(new BigDecimal("150.00"))
                .build();

        Movement m2 = Movement.builder()
                .id(UUID.randomUUID())
                .accountId(acc.getId())
                .date(LocalDateTime.now())
                .movementType(MovementType.RETIRO)
                .value(new BigDecimal("-30.00"))
                .balance(new BigDecimal("120.00"))
                .build();

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(accountRepository.findByClientId(clientId)).thenReturn(List.of(acc));
        when(movementRepository.findByAccountIdAndDateRange(any(), any(), any()))
                .thenReturn(List.of(m1, m2));

        Report report = reportService.generateReport(clientId, LocalDate.now().minusDays(1), LocalDate.now());

        assertThat(report.getTotalCredits()).isEqualByComparingTo("50.00");
        assertThat(report.getTotalDebits()).isEqualByComparingTo("30.00");
        assertThat(report.getTotalBalance()).isEqualByComparingTo("120.00");
        assertThat(report.getAccountStatements()).hasSize(1);
        assertThat(report.getAccountStatements().get(0).getMovements()).hasSize(2);
    }

        @Test
        void generateReportPdf_returnsPdfBytes() {
        UUID clientId = UUID.randomUUID();
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(Client.builder().id(clientId).name("J").identification("1").state(true).build()));
        when(accountRepository.findByClientId(clientId)).thenReturn(List.of());
                when(pdfGeneratorPort.generatePdf(any())).thenReturn("PDF".getBytes());

                byte[] pdfBytes = reportService.generateReportPdf(clientId, LocalDate.now(), LocalDate.now());
                assertThat(pdfBytes).isNotNull();
                assertThat(new String(pdfBytes)).isEqualTo("PDF");
    }
}
