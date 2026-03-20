package com.csdn.meeting.infrastructure.client;

import com.csdn.meeting.domain.port.AIParseResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@DisplayName("AIServiceClient: should call AI and parse fields")
class AIServiceClientTest {

    @Test
    @DisplayName("image parse: calls DoubaoClient.callWithImage and maps JSON fields")
    void parse_image_callsDoubaoAndMapsFields() {
        DoubaoClient doubaoClient = mock(DoubaoClient.class);
        String aiJson = "{"
                + "\"title\":\"CSDN AI 峰会\","
                + "\"description\":\"年度AI技术交流活动\","
                + "\"organizer\":\"CSDN\","
                + "\"format\":\"线上\","
                + "\"scene\":\"开发者会议\","
                + "\"venue\":\"北京\","
                + "\"regions\":\"北京\","
                + "\"tags\":[\"AI\",\"大模型\"],"
                + "\"targetAudience\":\"开发者\""
                + "}";
        when(doubaoClient.callWithImage(any(byte[].class), anyString(), anyString())).thenReturn(aiJson);

        AIServiceClient client = new AIServiceClient(doubaoClient);
        AIParseResult result = client.parse("fake-image".getBytes(), "poster.png");

        verify(doubaoClient).callWithImage(any(byte[].class), anyString(), anyString());
        assertEquals("CSDN AI 峰会", result.getTitle());
        assertEquals("年度AI技术交流活动", result.getDescription());
        assertEquals("CSDN", result.getOrganizer());
        assertEquals("线上", result.getFormat());
        assertEquals("开发者会议", result.getScene());
        assertEquals("北京", result.getVenue());
        assertEquals("北京", result.getRegions());
        assertEquals("开发者", result.getTargetAudience());
        assertEquals(2, result.getTags().size());
    }

    @Test
    @DisplayName("image parse: empty AI payload should throw to avoid silent empty 200")
    void parse_image_emptyPayload_throws() {
        DoubaoClient doubaoClient = mock(DoubaoClient.class);
        when(doubaoClient.callWithImage(any(byte[].class), anyString(), anyString())).thenReturn("{}");

        AIServiceClient client = new AIServiceClient(doubaoClient);
        assertThrows(RuntimeException.class, () -> client.parse("fake".getBytes(), "poster.jpg"));
    }

    @Test
    @DisplayName("image parse: title empty should fallback from description first sentence")
    void parse_image_titleEmpty_fallbackFromDescription() {
        DoubaoClient doubaoClient = mock(DoubaoClient.class);
        String aiJson = "{"
                + "\"title\":\"\","
                + "\"description\":\"CSDN 开发者大会，聚焦 AI 工程化实践。\","
                + "\"organizer\":\"CSDN\""
                + "}";
        when(doubaoClient.callWithImage(any(byte[].class), anyString(), anyString())).thenReturn(aiJson);

        AIServiceClient client = new AIServiceClient(doubaoClient);
        AIParseResult result = client.parse("fake-image".getBytes(), "会议海报.png");

        assertEquals("CSDN 开发者大会", result.getTitle());
        assertEquals("CSDN", result.getOrganizer());
    }

    @Test
    @DisplayName("image parse: first round empty then OCR fallback succeeds")
    void parse_image_firstEmpty_thenOcrFallback() {
        DoubaoClient doubaoClient = mock(DoubaoClient.class);
        when(doubaoClient.callWithImage(any(byte[].class), anyString(), anyString()))
                .thenReturn("{}")
                .thenReturn("会议标题：CSDN AI 大会\n主办方：CSDN\n地点：北京");
        when(doubaoClient.callText(anyString()))
                .thenReturn("{\"title\":\"CSDN AI 大会\",\"organizer\":\"CSDN\",\"venue\":\"北京\"}");

        AIServiceClient client = new AIServiceClient(doubaoClient);
        AIParseResult result = client.parse("fake-image".getBytes(), "poster.png");

        assertEquals("CSDN AI 大会", result.getTitle());
        assertEquals("CSDN", result.getOrganizer());
        assertEquals("北京", result.getVenue());
        verify(doubaoClient, times(2)).callWithImage(any(byte[].class), anyString(), anyString());
        verify(doubaoClient, times(1)).callText(anyString());
    }
}
