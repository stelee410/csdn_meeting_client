package com.csdn.meeting.infrastructure.util;

/**
 * 将逗号分隔的 targetAudience 转为 MySQL JSON 列所需的合法 JSON 数组字符串。
 */
public final class TargetAudienceJsonConverter {

    private TargetAudienceJsonConverter() {}

    /**
     * 若值为 null/空返回 null；若已是 JSON 数组（以 [ 开头）直接返回；否则按逗号拆分后转为 JSON 数组。
     */
    public static String toJson(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        String t = value.trim();
        if (t.startsWith("[")) return t;
        String[] parts = t.split(",");
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < parts.length; i++) {
            sb.append("\"").append(parts[i].trim().replace("\\", "\\\\").replace("\"", "\\\"")).append("\"");
            if (i < parts.length - 1) sb.append(",");
        }
        return sb.append("]").toString();
    }
}
