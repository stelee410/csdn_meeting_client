package com.csdn.meeting.application.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UrlNormalizer")
class UrlNormalizerTest {

    @Test
    @DisplayName("去除路径双斜杠")
    void normalize_doubleSlash() {
        String url = "http://localhost:8080//uploads/images//2026/x.png";
        assertEquals("http://localhost:8080/uploads/images/2026/x.png", UrlNormalizer.normalizeImageUrl(url));
    }

    @Test
    @DisplayName("保留 http:// 协议")
    void normalize_preservesProtocol() {
        String url = "http://example.com/uploads/a.png";
        assertEquals("http://example.com/uploads/a.png", UrlNormalizer.normalizeImageUrl(url));
    }

    @Test
    @DisplayName("保留 https:// 协议")
    void normalize_preservesHttps() {
        String url = "https://example.com//images/x.png";
        assertEquals("https://example.com/images/x.png", UrlNormalizer.normalizeImageUrl(url));
    }

    @Test
    @DisplayName("null 或空串返回原值")
    void normalize_nullOrBlank() {
        assertNull(UrlNormalizer.normalizeImageUrl(null));
        assertEquals("", UrlNormalizer.normalizeImageUrl(""));
    }
}
