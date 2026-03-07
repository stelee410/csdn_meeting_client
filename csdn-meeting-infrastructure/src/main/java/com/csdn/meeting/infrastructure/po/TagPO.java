package com.csdn.meeting.infrastructure.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 标签持久化对象
 * 对应数据库表 t_tag
 */
@Data
@TableName("t_tag")
public class TagPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标签名称
     */
    @TableField("tag_name")
    private String tagName;

    /**
     * 标签分类：tech-技术, scene-场景, topic-主题, brand-品牌
     */
    @TableField("tag_category")
    private String tagCategory;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    /**
     * 创建人ID todo
     */
//    @TableField(value = "create_by", fill = FieldFill.INSERT)
//    private String createBy;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateAt;

    /**
     * 更新人ID todo
     */
//    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
//    private String updateBy;

    /**
     * 软删除标志：0-未删除, 1-已删除
     */
    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    @TableLogic
    private Integer isDeleted;
}
