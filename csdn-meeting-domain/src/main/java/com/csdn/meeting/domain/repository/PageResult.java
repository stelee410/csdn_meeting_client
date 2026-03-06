package com.csdn.meeting.domain.repository;

import java.util.Collections;
import java.util.List;

/**
 * 分页结果，供仓储层返回分页数据，避免 domain 依赖 Spring Data。
 */
public class PageResult<T> {

    private final List<T> content;
    private final long totalElements;
    private final int page;
    private final int size;

    public PageResult(List<T> content, long totalElements, int page, int size) {
        this.content = content == null ? Collections.emptyList() : Collections.unmodifiableList(content);
        this.totalElements = totalElements;
        this.page = page;
        this.size = size;
    }

    public List<T> getContent() {
        return content;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public int getTotalPages() {
        return size <= 0 ? 0 : (int) ((totalElements + size - 1) / size);
    }

    public boolean hasNext() {
        return (page + 1) * size < totalElements;
    }

    public boolean hasPrevious() {
        return page > 0;
    }
}
