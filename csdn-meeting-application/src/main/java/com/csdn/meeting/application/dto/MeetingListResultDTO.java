package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * 会议列表查询结果DTO
 * 支持泛型，根据viewMode返回不同的Item类型
 */
@Schema(description = "会议列表查询结果，包含分页信息和会议列表数据")
public class MeetingListResultDTO<T> {

    /**
     * 总记录数
     */
    @Schema(description = "总记录数", example = "100")
    private long total;

    /**
     * 当前页码（从0开始）
     */
    @Schema(description = "当前页码，从0开始", example = "0")
    private int page;

    /**
     * 每页大小
     */
    @Schema(description = "每页大小", example = "20")
    private int size;

    /**
     * 总页数
     */
    @Schema(description = "总页数", example = "5")
    private int totalPages;

    /**
     * 当前视图模式
     */
    @Schema(description = "当前视图模式：card（阅读视图）/list（列表视图）", example = "card")
    private String viewMode;

    /**
     * 会议列表数据
     */
    @Schema(description = "会议列表数据，根据viewMode返回MeetingCardItemDTO或MeetingListItemDTO")
    private List<T> items;

    /**
     * 是否为空结果
     */
    @Schema(description = "是否为空结果", example = "false")
    private boolean empty;

    /**
     * 空状态提示语
     */
    @Schema(description = "空状态提示语", example = "暂无相关会议，可进入感兴趣的会议详情页订阅标签获取推送")
    private String emptyTip;

    /**
     * 建议操作提示
     */
    @Schema(description = "建议操作提示列表")
    private List<String> suggestions;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
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

    public String getViewMode() {
        return viewMode;
    }

    public void setViewMode(String viewMode) {
        this.viewMode = viewMode;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public String getEmptyTip() {
        return emptyTip;
    }

    public void setEmptyTip(String emptyTip) {
        this.emptyTip = emptyTip;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    /**
     * 便捷方法：创建空结果
     */
    public static <T> MeetingListResultDTO<T> empty(String viewMode, String emptyTip) {
        MeetingListResultDTO<T> result = new MeetingListResultDTO<>();
        result.setTotal(0);
        result.setPage(0);
        result.setSize(0);
        result.setTotalPages(0);
        result.setViewMode(viewMode);
        result.setItems(java.util.Collections.emptyList());
        result.setEmpty(true);
        result.setEmptyTip(emptyTip);
        result.setSuggestions(java.util.Arrays.asList(
            "尝试其他筛选条件",
            "进入感兴趣的会议详情页订阅标签获取推送"
        ));
        return result;
    }
}
