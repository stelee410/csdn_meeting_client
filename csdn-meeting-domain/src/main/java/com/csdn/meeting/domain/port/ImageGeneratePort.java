package com.csdn.meeting.domain.port;

/**
 * 会议背景图生成端口：根据会议标题和简介，生成适合的封面图 URL。
 */
public interface ImageGeneratePort {

    /**
     * 生成会议背景图。
     *
     * @param title       会议标题
     * @param description 会议简介（可为空）
     * @return 可直接访问的图片 URL
     */
    String generate(String title, String description);
}
