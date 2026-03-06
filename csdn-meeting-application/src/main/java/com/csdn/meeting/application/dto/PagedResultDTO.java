package com.csdn.meeting.application.dto;

import com.csdn.meeting.domain.repository.PageResult;

import java.util.List;

/**
 * 分页结果 DTO，用于 API 响应
 */
public class PagedResultDTO<T> {

    private List<T> content;
    private long totalElements;
    private int page;
    private int size;
    private int totalPages;

    public PagedResultDTO() {
    }

    public static <T> PagedResultDTO<T> from(PageResult<T> pageResult) {
        PagedResultDTO<T> dto = new PagedResultDTO<>();
        dto.setContent(pageResult.getContent());
        dto.setTotalElements(pageResult.getTotalElements());
        dto.setPage(pageResult.getPage());
        dto.setSize(pageResult.getSize());
        dto.setTotalPages(pageResult.getTotalPages());
        return dto;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
