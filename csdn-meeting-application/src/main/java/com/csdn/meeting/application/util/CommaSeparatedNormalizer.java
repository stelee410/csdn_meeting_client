package com.csdn.meeting.application.util;

/**
 * 逗号分隔字符串规范化：将全角逗号统一替换为半角逗号，便于存储与解析。
 * 用于目标人群、技术标签等支持"字符串，逗号隔开"的字段。
 */
public final class CommaSeparatedNormalizer {

    private static final char FULL_WIDTH_COMMA = '\uff0c'; // 全角逗号 ，
    private static final char HALF_WIDTH_COMMA = ',';      // 半角逗号 ,

    private CommaSeparatedNormalizer() {
    }

    /**
     * 规范化逗号分隔字符串：全角逗号替换为半角逗号。
     *
     * @param value 输入字符串，可为 null
     * @return 规范化后的字符串，null 则返回 null
     */
    public static String normalize(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return value.replace(FULL_WIDTH_COMMA, HALF_WIDTH_COMMA);
    }
}
