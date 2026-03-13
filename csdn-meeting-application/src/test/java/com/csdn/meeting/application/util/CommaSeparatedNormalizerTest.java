package com.csdn.meeting.application.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CommaSeparatedNormalizer")
class CommaSeparatedNormalizerTest {

    @Test
    @DisplayName("全角逗号替换为半角")
    void normalize_fullWidthToHalfWidth() {
        assertEquals("Java,Python,Go", CommaSeparatedNormalizer.normalize("Java，Python，Go"));
    }

    @Test
    @DisplayName("半角逗号保持不变")
    void normalize_halfWidthUnchanged() {
        assertEquals("Java,Python,Go", CommaSeparatedNormalizer.normalize("Java,Python,Go"));
    }

    @Test
    @DisplayName("全角半角混合")
    void normalize_mixedCommas() {
        assertEquals("a,b,c", CommaSeparatedNormalizer.normalize("a，b,c"));
    }

    @Test
    @DisplayName("null 返回 null")
    void normalize_nullReturnsNull() {
        assertNull(CommaSeparatedNormalizer.normalize(null));
    }

    @Test
    @DisplayName("空串返回空串")
    void normalize_emptyReturnsEmpty() {
        assertEquals("", CommaSeparatedNormalizer.normalize(""));
    }
}
