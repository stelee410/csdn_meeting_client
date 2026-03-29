package com.csdn.meeting.interfaces.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 消息分页结果视图对象
 */
@Schema(description = "消息分页结果")
public class MessagePageResult {

    @Schema(description = "消息列表")
    private List<MessageVO> list;

    @Schema(description = "总记录数", example = "100")
    private Long total;

    @Schema(description = "当前页码", example = "1")
    private Integer page;

    @Schema(description = "每页大小", example = "20")
    private Integer size;

    @Schema(description = "总页数", example = "5")
    private Integer totalPages;

    public List<MessageVO> getList() {
        return list;
    }

    public void setList(List<MessageVO> list) {
        this.list = list;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }
}
