package com.csdn.meeting.application.util;

/**
 * 图片 URL 规范化，修复 issue001 双斜杠及路径拼接问题
 */
public final class UrlNormalizer {

    private UrlNormalizer() {
    }

    /**
     * 规范化图片 URL，去除路径中的双斜杠（保留 http:// 或 https://）
     */
    public static String normalizeImageUrl(String url) {
        if (url == null || url.trim().isEmpty()) return url;
        String s = url.trim();
        int protocolEnd = s.indexOf("://");
        if (protocolEnd >= 0) {
            String prefix = s.substring(0, protocolEnd + 3);
            String path = s.substring(protocolEnd + 3);
            while (path.contains("//")) {
                path = path.replace("//", "/");
            }
            return prefix + path;
        }
        while (s.contains("//")) {
            s = s.replace("//", "/");
        }
        return s;
    }
}
