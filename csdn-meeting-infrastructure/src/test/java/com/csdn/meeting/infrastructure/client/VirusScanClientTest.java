package com.csdn.meeting.infrastructure.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("VirusScanClient: stub returns OK for all files")
class VirusScanClientTest {

    private VirusScanClient client;

    @BeforeEach
    void setUp() {
        client = new VirusScanClient();
    }

    @Test
    @DisplayName("scan does not throw for any file")
    void scan_doesNotThrow() {
        client.scan("test content".getBytes(), "file.pdf");
    }
}
