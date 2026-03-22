package com.csdn.meeting.application.service;

import com.csdn.meeting.application.dto.AIParseResultDTO;
import com.csdn.meeting.application.exception.AIParseException;
import com.csdn.meeting.application.exception.BusinessException;
import com.csdn.meeting.domain.port.AIParsePort;
import com.csdn.meeting.domain.port.AIParseResult;
import com.csdn.meeting.domain.port.SensitiveWordFilterPort;
import com.csdn.meeting.domain.port.VirusScanPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AIParsingUseCase: parse, virus scan, timeout, sensitive filter")
class AIParsingUseCaseTest {

    @Mock
    private VirusScanPort virusScanPort;
    @Mock
    private AIParsePort aiParsePort;
    @Mock
    private SensitiveWordFilterPort sensitiveWordFilterPort;
    private AIParsingUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new AIParsingUseCase(virusScanPort, aiParsePort, sensitiveWordFilterPort, 2);
    }

    @Test
    @DisplayName("parse: full flow returns AIParseResultDTO with traceId, filledFields, sensitiveFields, data")
    void parse_fullFlow_returnsDTO() {
        byte[] fileBytes = "test".getBytes();
        String fileName = "meeting.pdf";

        AIParseResult mockResult = new AIParseResult();
        mockResult.setTitle("技术大会");
        mockResult.setDescription("年度大会");
        mockResult.setOrganizer("CSDN");
        mockResult.setFormat("OFFLINE");
        mockResult.setVenue("北京");
        when(aiParsePort.parse(any(byte[].class), eq(fileName))).thenReturn(mockResult);
        when(sensitiveWordFilterPort.findSensitiveFields(any(AIParseResult.class))).thenReturn(Collections.emptyList());

        AIParseResultDTO result = useCase.parse(fileBytes, fileName);

        assertNotNull(result.getTraceId());
        assertFalse(result.getTraceId().isEmpty());
        assertNotNull(result.getFilledFields());
        assertTrue(result.getFilledFields().contains("title"));
        assertTrue(result.getFilledFields().contains("description"));
        assertTrue(result.getFilledFields().contains("organizer"));
        assertTrue(result.getFilledFields().contains("format"));
        assertTrue(result.getFilledFields().contains("venue"));
        assertNotNull(result.getSensitiveFields());
        assertTrue(result.getSensitiveFields().isEmpty());
        assertNotNull(result.getData());
        assertEquals("技术大会", result.getData().getTitle());
        assertEquals("年度大会", result.getData().getDescription());
        assertEquals("CSDN", result.getData().getOrganizer());
        assertEquals("OFFLINE", result.getData().getFormat());
        assertEquals("北京", result.getData().getVenue());
        verify(virusScanPort).scan(fileBytes, fileName);
        verify(aiParsePort).parse(fileBytes, fileName);
        verify(sensitiveWordFilterPort).findSensitiveFields(mockResult);
    }

    @Test
    @DisplayName("parse: virus scan failure throws BusinessException 400")
    void parse_virusScanFails_throws400() {
        doThrow(new RuntimeException("Virus detected")).when(virusScanPort).scan(any(byte[].class), anyString());

        BusinessException ex = assertThrows(BusinessException.class, () ->
                useCase.parse("test".getBytes(), "file.pdf"));

        assertEquals(400, ex.getHttpStatus());
        assertTrue(ex.getMessage().contains("病毒扫描失败"));
        verify(aiParsePort, never()).parse(any(), anyString());
    }

    @Test
    @DisplayName("parse: AI parse timeout throws AIParseException 422")
    void parse_timeout_throwsAIParseException() {
        when(aiParsePort.parse(any(byte[].class), anyString()))
                .thenAnswer(inv -> {
                    TimeUnit.SECONDS.sleep(5);
                    return new AIParseResult();
                });

        AIParseException ex = assertThrows(AIParseException.class, () ->
                useCase.parse("test".getBytes(), "file.pdf"));

        assertEquals(422, ex.getHttpStatus());
        assertTrue(ex.getMessage().contains("超时"));
    }

    @Test
    @DisplayName("parse: sensitive fields returned in result")
    void parse_sensitiveFields_returned() {
        AIParseResult mockResult = new AIParseResult();
        mockResult.setTitle("技术大会");
        mockResult.setDescription("敏感内容");
        when(aiParsePort.parse(any(byte[].class), anyString())).thenReturn(mockResult);
        when(sensitiveWordFilterPort.findSensitiveFields(any(AIParseResult.class)))
                .thenReturn(Arrays.asList("description", "title"));

        AIParseResultDTO result = useCase.parse("test".getBytes(), "meeting.pdf");

        assertEquals(2, result.getSensitiveFields().size());
        assertTrue(result.getSensitiveFields().contains("description"));
        assertTrue(result.getSensitiveFields().contains("title"));
    }

    @Test
    @DisplayName("parse: AI port throws converts to AIParseException 422")
    void parse_aiPortThrows_convertsToAIParseException() {
        when(aiParsePort.parse(any(byte[].class), anyString()))
                .thenThrow(new RuntimeException("LLM service unavailable"));

        AIParseException ex = assertThrows(AIParseException.class, () ->
                useCase.parse("test".getBytes(), "file.pdf"));

        assertEquals(422, ex.getHttpStatus());
        assertTrue(ex.getMessage().contains("解析失败"));
    }

    @Test
    @DisplayName("parse: stub AI returns empty result, filledFields computed correctly")
    void parse_stubReturnsEmpty_computesFilledFields() {
        when(aiParsePort.parse(any(byte[].class), anyString())).thenReturn(new AIParseResult());
        when(sensitiveWordFilterPort.findSensitiveFields(any(AIParseResult.class))).thenReturn(Collections.emptyList());

        AIParseResultDTO result = useCase.parse("test".getBytes(), "file.pdf");

        assertNotNull(result.getTraceId());
        assertTrue(result.getFilledFields().isEmpty());
        assertNotNull(result.getData());
    }
}
