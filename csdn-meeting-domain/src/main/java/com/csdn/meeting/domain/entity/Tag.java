package com.csdn.meeting.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 标签实体
 * 对应标签表，用于会议标签化和用户订阅
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Tag extends BaseEntity {

    /**
     * 标签ID
     */
    private Long id;

    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 标签分类
     */
    private TagCategory tagCategory;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人ID
     */
    private String createBy;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 更新人ID
     */
    private String updateBy;

    /**
     * 标签分类枚举
     */
    public enum TagCategory {
        TECH("技术"),
        SCENE("场景"),
        TOPIC("主题"),
        BRAND("品牌"),
        TYPE("类型");

        private final String displayName;

        TagCategory(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
