package com.pichincha.accounts.infrastructure.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationConfigTest {

    @Test
    void shouldCreateObjectMapperWithJavaTimeSupportAndNonNullInclusion() throws Exception {
        ApplicationConfig config = new ApplicationConfig();
        ObjectMapper mapper = config.objectMapper();

        assertThat(mapper).isNotNull();
        // Java time supported: should serialize LocalDate without failing
        String out = mapper.writeValueAsString(LocalDate.of(2024, 1, 1));
        assertThat(out).isNotBlank();
        // Non-null inclusion
        assertThat(mapper.getSerializationConfig().getDefaultPropertyInclusion().getValueInclusion())
                .isEqualTo(JsonInclude.Include.NON_NULL);
    }
}
