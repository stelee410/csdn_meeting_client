package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 分页结果DTO
 */
@Data
@Schema(description = "分页结果")
public class PageResult<T> {

    @Schema(description = "总记录数", example = "100")
    private Long total;

    @Schema(description = "总页数", example = "10")
    private Long pages;

    @Schema(description = "当前页码", example = "1")
    private Long current;

    @Schema(description = "每页大小", example = "10")
    private Long size;

    @Schema(description = "数据列表")
    private List<T> records;

    /**
     * 构建分页结果
     */
    public static <T> PageResult<T> of(long total, long current, long size, List<T> records) {
        PageResult<T> result = new PageResult<>();
        result.setTotal(total);
        result.setPages((total + size - 1) / size);
        result.setCurrent(current);
        result.setSize(size);
        result.setRecords(records);
        return result;
    }

    /**
     * 构建空分页结果
     */
    public static <T> PageResult<T> empty() {
        PageResult<T> result = new PageResult<>();
        result.setTotal(0L);
        result.setPages(0L);
        result.setCurrent(1L);
        result.setSize(10L);
        result.setRecords(java.util.Collections.emptyList());
        return result;
    }
}
