package com.pichincha.accounts.application.port.output;

import com.pichincha.accounts.domain.Movement;

import java.util.List;

public interface ReportedOutputPort {
    String generateReport(List<Movement> movements);
}
