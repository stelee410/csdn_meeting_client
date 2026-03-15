package com.csdn.meeting.infrastructure.po;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus entity for t_dictionary (V14 migration).
 */
@TableName("t_dictionary")
public class DictionaryPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("dict_type")
    private String dictType;

    @TableField("item_code")
    private String itemCode;

    @TableField("item_label")
    private String itemLabel;

    @TableField("sort_order")
    private Integer sortOrder;

    @TableField("is_active")
    private Boolean isActive;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDictType() { return dictType; }
    public void setDictType(String dictType) { this.dictType = dictType; }

    public String getItemCode() { return itemCode; }
    public void setItemCode(String itemCode) { this.itemCode = itemCode; }

    public String getItemLabel() { return itemLabel; }
    public void setItemLabel(String itemLabel) { this.itemLabel = itemLabel; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
