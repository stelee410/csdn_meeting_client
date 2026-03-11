package com.csdn.meeting.domain.port;

/**
 * 图片存储端口：解耦存储介质（本地磁盘、OSS 等均可实现此接口）
 */
public interface ImageStoragePort {

    /**
     * 存储图片并返回可访问的 URL
     *
     * @param imageBytes       图片二进制内容
     * @param originalFileName 原始文件名（含扩展名，用于判断格式）
     * @return 图片的可访问 URL
     */
    String store(byte[] imageBytes, String originalFileName);
}
