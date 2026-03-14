package com.csdn.meeting.infrastructure.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TargetAudienceJsonConverter: 逗号分隔 -> MySQL JSON 数组")
class TargetAudienceJsonConverterTest {

    @Test
    @DisplayName("逗号分隔字符串转 JSON 数组")
    void commaSeparated_toJsonArray() {
        assertEquals("[\"developer\",\"architect\",\"product_manager\"]",
                TargetAudienceJsonConverter.toJson("developer,architect,product_manager"));
    }

    @Test
    @DisplayName("null 返回 null")
    void null_returnsNull() {
        assertNull(TargetAudienceJsonConverter.toJson(null));
    }

    @Test
    @DisplayName("空字符串返回 null")
    void empty_returnsNull() {
        assertNull(TargetAudienceJsonConverter.toJson(""));
        assertNull(TargetAudienceJsonConverter.toJson("   "));
    }

    @Test
    @DisplayName("已是 JSON 数组则原样返回")
    void alreadyJsonArray_returnsAsIs() {
        String json = "[\"a\",\"b\"]";
        assertEquals(json, TargetAudienceJsonConverter.toJson(json));
    }

    @Test
    @DisplayName("单个值转 JSON 数组")
    void singleValue_toJsonArray() {
        assertEquals("[\"developer\"]", TargetAudienceJsonConverter.toJson("developer"));
    }

    @Test
    @DisplayName("含空格的逗号分隔正确 trim")
    void withSpaces_trimsCorrectly() {
        assertEquals("[\"developer\",\"architect\"]",
                TargetAudienceJsonConverter.toJson(" developer , architect "));
    }
}
