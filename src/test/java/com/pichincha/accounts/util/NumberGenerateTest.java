package com.pichincha.accounts.util;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NumberGenerateTest {

    @RepeatedTest(3)
    void shouldGenerateAccountNumberWithSixDigits() {
        String number = NumberGenerate.generateAccountNumber();
        assertThat(number).hasSize(6).matches("\\d{6}");
    }

    @RepeatedTest(3)
    void shouldGenerateClientIdWithEightDigits() {
        String clientId = NumberGenerate.generateClientId();
        assertThat(clientId).hasSize(8).matches("\\d{8}");
    }

    @Test
    void shouldGenerateDifferentValuesMostOfTheTime() {
        String a1 = NumberGenerate.generateAccountNumber();
        String a2 = NumberGenerate.generateAccountNumber();
        String c1 = NumberGenerate.generateClientId();
        String c2 = NumberGenerate.generateClientId();
        // No garantizamos unicidad, pero probabilísticamente deben diferir
        assertThat(a1).isNotEqualTo(a2);
        assertThat(c1).isNotEqualTo(c2);
    }
}
