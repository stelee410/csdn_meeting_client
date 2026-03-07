package com.csdn.meeting.application.service;

import com.csdn.meeting.application.dto.*;
import com.csdn.meeting.application.utils.TimeRangeCalculator;
import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.domain.repository.MeetingRepository;
import com.csdn.meeting.domain.repository.MeetingSearchRepository;
import com.csdn.meeting.domain.valueobject.MeetingFormat;
import com.csdn.meeting.domain.valueobject.MeetingScene;
import com.csdn.meeting.domain.valueobject.MeetingTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 会议列表查询UseCase
 * 支持多维度筛选、关键词搜索、分页、双视图切换
 */
@Service
public class MeetingListUseCase {

    private static final Logger logger = LoggerFactory.getLogger(MeetingListUseCase.class);

    private final MeetingSearchRepository meetingSearchRepository;
    private final MeetingRepository meetingRepository;
    private final TimeRangeCalculator timeRangeCalculator;
    private final MeetingAnalyticsService analyticsService;

    public MeetingListUseCase(MeetingSearchRepository meetingSearchRepository,
                              MeetingRepository meetingRepository,
                              TimeRangeCalculator timeRangeCalculator,
                              MeetingAnalyticsService analyticsService) {
        this.meetingSearchRepository = meetingSearchRepository;
        this.meetingRepository = meetingRepository;
        this.timeRangeCalculator = timeRangeCalculator;
        this.analyticsService = analyticsService;
    }

    /**
     * 查询会议列表
     *
     * @param query 查询参数
     * @return 列表结果
     */
    public MeetingListResultDTO<?> queryMeetingList(MeetingListQueryDTO query) {
        // 记录埋点
        if (query.getUserId() != null) {
            analyticsService.trackViewSwitch(String.valueOf(query.getUserId()), query.getViewMode());
            analyticsService.trackFilter(String.valueOf(query.getUserId()), "all", buildFilterDesc(query));
        }

        // 转换筛选条件
        Integer formatCode = parseFormatCode(query.getFormat());
        Integer typeCode = parseTypeCode(query.getType());
        Integer sceneCode = parseSceneCode(query.getScene());

        // 计算时间范围
        LocalDateTime[] timeRange = timeRangeCalculator.calculateTimeRange(query.getTimeRange());
        LocalDateTime startTimeFrom = timeRange != null ? timeRange[0] : null;
        LocalDateTime startTimeTo = timeRange != null ? timeRange[1] : null;

        // 查询数据
        List<Meeting> meetings = meetingSearchRepository.findMeetingList(
                formatCode, typeCode, sceneCode,
                startTimeFrom, startTimeTo,
                query.getKeyword(),
                query.getPage(), query.getSize()
        );

        // 查询总数
        long total = meetingSearchRepository.countMeetingList(
                formatCode, typeCode, sceneCode,
                startTimeFrom, startTimeTo,
                query.getKeyword()
        );

        // 转换结果
        if (meetings.isEmpty()) {
            return MeetingListResultDTO.empty(query.getViewMode(), "暂无相关会议，可进入感兴趣的会议详情页订阅标签获取推送");
        }

        return buildResult(query, meetings, total);
    }

    /**
     * 获取筛选选项枚举值
     */
    public FilterOptionsDTO getFilterOptions() {
        FilterOptionsDTO options = new FilterOptionsDTO();

        // 会议形式
        options.setFormatOptions(Arrays.asList(
                new FilterOptionsDTO.FilterOption("", "全部"),
                new FilterOptionsDTO.FilterOption("ONLINE", "线上"),
                new FilterOptionsDTO.FilterOption("OFFLINE", "线下"),
                new FilterOptionsDTO.FilterOption("HYBRID", "线上+线下")
        ));

        // 会议类型
        options.setTypeOptions(Arrays.asList(
                new FilterOptionsDTO.FilterOption("", "全部"),
                new FilterOptionsDTO.FilterOption("SUMMIT", "技术峰会"),
                new FilterOptionsDTO.FilterOption("SALON", "技术沙龙"),
                new FilterOptionsDTO.FilterOption("WORKSHOP", "技术研讨会")
        ));

        // 会议场景
        options.setSceneOptions(Arrays.asList(
                new FilterOptionsDTO.FilterOption("", "全部"),
                new FilterOptionsDTO.FilterOption("DEVELOPER", "开发者会议"),
                new FilterOptionsDTO.FilterOption("INDUSTRY", "产业会议"),
                new FilterOptionsDTO.FilterOption("PRODUCT", "产品发布会议"),
                new FilterOptionsDTO.FilterOption("REGIONAL", "区域营销会议"),
                new FilterOptionsDTO.FilterOption("UNIVERSITY", "高校会议")
        ));

        // 召开时间
        options.setTimeRangeOptions(Arrays.asList(
                new FilterOptionsDTO.FilterOption("", "全部"),
                new FilterOptionsDTO.FilterOption("THIS_WEEK", "本周"),
                new FilterOptionsDTO.FilterOption("THIS_MONTH", "本月"),
                new FilterOptionsDTO.FilterOption("NEXT_3_MONTHS", "未来三个月")
        ));

        // 视图模式
        options.setViewModeOptions(Arrays.asList(
                new FilterOptionsDTO.FilterOption("card", "阅读视图", "大图卡片展示"),
                new FilterOptionsDTO.FilterOption("list", "列表视图", "紧凑列表展示")
        ));

        return options;
    }

    /**
     * 构建查询结果
     */
    private MeetingListResultDTO<?> buildResult(MeetingListQueryDTO query,
                                                List<Meeting> meetings,
                                                long total) {
        MeetingListResultDTO<Object> result = new MeetingListResultDTO<>();
        result.setTotal(total);
        result.setPage(query.getPage());
        result.setSize(query.getSize());
        result.setTotalPages((int) Math.ceil((double) total / query.getSize()));
        result.setViewMode(query.getViewMode());
        result.setEmpty(false);

        // 根据视图模式转换数据
        if (query.isListView()) {
            List<MeetingListItemDTO> items = meetings.stream()
                    .map(this::toListItemDTO)
                    .collect(Collectors.toList());
            result.setItems(Collections.singletonList(items));
        } else {
            List<MeetingCardItemDTO> items = meetings.stream()
                    .map(this::toCardItemDTO)
                    .collect(Collectors.toList());
            result.setItems(Collections.singletonList(items));
        }

        return result;
    }

    /**
     * 转换为列表视图DTO
     */
    private MeetingListItemDTO toListItemDTO(Meeting meeting) {
        MeetingListItemDTO dto = new MeetingListItemDTO();
        dto.setId(meeting.getId());
        dto.setMeetingId(meeting.getMeetingId());
        dto.setTitle(meeting.getTitle());
        dto.setPosterUrl(meeting.getPosterUrl());

        // 状态
        if (meeting.getStatus() != null) {
            dto.setStatus(meeting.getStatus().name());
            dto.setStatusDisplay(meeting.getStatus().getDisplayName());
        }

        // 时间
        dto.setStartTime(meeting.getStartTime());
        if (meeting.getStartTime() != null) {
            dto.setStartTimeDisplay(meeting.getStartTime().format(DateTimeFormatter.ofPattern("MM-dd HH:mm")));
        }

        // 地点
        dto.setCityName(meeting.getCityName());
        dto.setVenue(meeting.getVenue());

        // 形式
        if (meeting.getFormat() != null) {
            dto.setFormat(meeting.getFormat().name());
            dto.setFormatDisplay(meeting.getFormat().getDisplayName());
        }

        // 热度
        dto.setHotScore(meeting.getHotScore());
        dto.setHotScoreDisplay(formatHotScore(meeting.getHotScore()));

        // 报名进度
        dto.setCurrentParticipants(meeting.getCurrentParticipants());
        dto.setMaxParticipants(meeting.getMaxParticipants());
        dto.setParticipantsDisplay(meeting.getParticipantsDisplay());

        return dto;
    }

    /**
     * 转换为卡片视图DTO
     */
    private MeetingCardItemDTO toCardItemDTO(Meeting meeting) {
        MeetingCardItemDTO dto = new MeetingCardItemDTO();
        dto.setId(meeting.getId());
        dto.setMeetingId(meeting.getMeetingId());
        dto.setTitle(meeting.getTitle());
        dto.setDescription(meeting.getDescription());
        dto.setCoverImage(meeting.getCoverImage());

        // 主办方
        // TODO: 从关联表查询完整主办方信息
        dto.setOrganizerName(meeting.getOrganizer());

        // 状态
        if (meeting.getStatus() != null) {
            dto.setStatus(meeting.getStatus().name());
            dto.setStatusDisplay(meeting.getStatus().getDisplayName());
        }

        // 时间
        dto.setStartTime(meeting.getStartTime());
        dto.setEndTime(meeting.getEndTime());
        if (meeting.getStartTime() != null && meeting.getEndTime() != null) {
            String start = meeting.getStartTime().format(DateTimeFormatter.ofPattern("MM-dd"));
            String end = meeting.getEndTime().format(DateTimeFormatter.ofPattern("MM-dd"));
            if (start.equals(end)) {
                dto.setTimeDisplay(start);
            } else {
                dto.setTimeDisplay(start + " ~ " + end);
            }
        }

        // 形式
        if (meeting.getFormat() != null) {
            dto.setFormat(meeting.getFormat().name());
            dto.setFormatDisplay(meeting.getFormat().getDisplayName());
        }

        // 类型
        dto.setMeetingType(String.valueOf(meeting.getMeetingType()));
        // TODO: 转换类型显示名称

        // 场景
        dto.setScene(meeting.getScene());
        // TODO: 转换场景显示名称

        // 热度
        dto.setHotScore(meeting.getHotScore());
        dto.setHotScoreDisplay(formatHotScore(meeting.getHotScore()));

        // 报名进度
        dto.setCurrentParticipants(meeting.getCurrentParticipants());
        dto.setMaxParticipants(meeting.getMaxParticipants());
        dto.setParticipantsDisplay(meeting.getParticipantsDisplay());

        // 地点
        dto.setCityName(meeting.getCityName());
        dto.setVenue(meeting.getVenue());

        return dto;
    }

    /**
     * 格式化热度显示
     */
    private String formatHotScore(Integer hotScore) {
        if (hotScore == null || hotScore == 0) {
            return "";
        }
        if (hotScore >= 1000) {
            return String.format("%.1fk人感兴趣", hotScore / 1000.0);
        }
        return hotScore + "人感兴趣";
    }

    /**
     * 解析会议形式代码
     */
    private Integer parseFormatCode(String format) {
        if (format == null || format.isEmpty()) {
            return null;
        }
        MeetingFormat mf = MeetingFormat.of(format);
        return mf != null ? mf.getCode() : null;
    }

    /**
     * 解析会议类型代码
     */
    private Integer parseTypeCode(String type) {
        if (type == null || type.isEmpty()) {
            return null;
        }
        MeetingTypeEnum mt = MeetingTypeEnum.of(type);
        return mt != null ? mt.getCode() : null;
    }

    /**
     * 解析会议场景代码
     */
    private Integer parseSceneCode(String scene) {
        if (scene == null || scene.isEmpty()) {
            return null;
        }
        MeetingScene ms = MeetingScene.of(scene);
        return ms != null ? ms.getCode() : null;
    }

    /**
     * 构建筛选描述（用于埋点）
     */
    private String buildFilterDesc(MeetingListQueryDTO query) {
        StringBuilder sb = new StringBuilder();
        if (query.getFormat() != null) {
            sb.append("format=").append(query.getFormat()).append(";");
        }
        if (query.getType() != null) {
            sb.append("type=").append(query.getType()).append(";");
        }
        if (query.getScene() != null) {
            sb.append("scene=").append(query.getScene()).append(";");
        }
        if (query.getTimeRange() != null) {
            sb.append("timeRange=").append(query.getTimeRange()).append(";");
        }
        if (query.getKeyword() != null) {
            sb.append("keyword=").append(query.getKeyword());
        }
        return sb.toString();
    }
}
