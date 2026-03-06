package com.csdn.meeting.infrastructure.client;

import com.csdn.meeting.domain.port.AIParseResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SensitiveWordFilter: stub returns empty list")
class SensitiveWordFilterTest {

    private SensitiveWordFilter filter;

    @BeforeEach
    void setUp() {
        filter = new SensitiveWordFilter();
    }

    @Test
    @DisplayName("findSensitiveFields returns empty list for any input")
    void findSensitiveFields_returnsEmpty() {
        AIParseResult result = new AIParseResult();
        result.setTitle("test");
        result.setDescription("content");

        List<String> sensitive = filter.findSensitiveFields(result);

        assertNotNull(sensitive);
        assertTrue(sensitive.isEmpty());
    }
}
