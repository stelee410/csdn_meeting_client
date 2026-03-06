package com.csdn.meeting.infrastructure.client;

import com.csdn.meeting.domain.port.AIParseResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AIServiceClient: stub returns empty/default result when no LLM configured")
class AIServiceClientTest {

    private AIServiceClient client;

    @BeforeEach
    void setUp() {
        client = new AIServiceClient();
    }

    @Test
    @DisplayName("parse returns non-null empty AIParseResult")
    void parse_returnsEmptyResult() {
        AIParseResult result = client.parse("test".getBytes(), "meeting.pdf");

        assertNotNull(result);
        assertNull(result.getTitle());
        assertNull(result.getDescription());
        assertTrue(result.getTags().isEmpty());
    }
}
