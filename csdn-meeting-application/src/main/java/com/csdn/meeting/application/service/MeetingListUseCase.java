package com.csdn.meeting.application.service;

import com.csdn.meeting.application.dto.*;
import com.csdn.meeting.application.utils.TimeRangeCalculator;
import java.util.List;
import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.domain.entity.Tag;
import com.csdn.meeting.domain.repository.MeetingRepository;
import com.csdn.meeting.domain.repository.MeetingSearchRepository;
import com.csdn.meeting.domain.repository.PageResult;
import com.csdn.meeting.domain.repository.TagRepository;
import com.csdn.meeting.domain.valueobject.MeetingFormat;
import com.csdn.meeting.domain.valueobject.MeetingScene;
import com.csdn.meeting.domain.valueobject.MeetingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 会议列表查询UseCase
 * 支持多维度筛选、关键词搜索、分页，统一返回卡片结构
 */
@Service
public class MeetingListUseCase {

    private static final Logger logger = LoggerFactory.getLogger(MeetingListUseCase.class);

    private final MeetingSearchRepository meetingSearchRepository;
    private final MeetingRepository meetingRepository;
    private final TagRepository tagRepository;
    private final TimeRangeCalculator timeRangeCalculator;
    private final DictionaryUseCase dictionaryUseCase;

    public MeetingListUseCase(MeetingSearchRepository meetingSearchRepository,
                              MeetingRepository meetingRepository,
                              TagRepository tagRepository,
                              TimeRangeCalculator timeRangeCalculator,
                              DictionaryUseCase dictionaryUseCase) {
        this.meetingSearchRepository = meetingSearchRepository;
        this.meetingRepository = meetingRepository;
        this.tagRepository = tagRepository;
        this.timeRangeCalculator = timeRangeCalculator;
        this.dictionaryUseCase = dictionaryUseCase;
    }

    /**
     * 查询会议列表
     *
     * @param query 查询参数
     * @return 列表结果（统一为 card 卡片结构）
     */
    public MeetingListResultDTO<MeetingCardItemDTO> queryMeetingList(MeetingListQueryDTO query) {
        // 转换筛选条件
        Integer formatCode = new Integer(0).equals(query.getFormat()) ? null : query.getFormat();
        Integer typeCode = new Integer(0).equals(query.getType()) ? null : query.getType();
        Integer sceneCode = new Integer(0).equals(query.getScene()) ? null : query.getScene();

        // 计算时间范围
        LocalDateTime[] timeRange = timeRangeCalculator.calculateTimeRange(query.getTimeRange());
        LocalDateTime startTimeFrom = timeRange != null ? timeRange[0] : null;
        LocalDateTime startTimeTo = timeRange != null ? timeRange[1] : null;

        // 一次分页查询（MyBatis-Plus IPage 返回列表 + 总数）
        PageResult<Meeting> pageResult = meetingSearchRepository.findMeetingList(
                formatCode, typeCode, sceneCode,
                startTimeFrom, startTimeTo,
                query.getKeyword(),
                query.getPage(), query.getSize());

        if (pageResult.getContent().isEmpty()) {
            return MeetingListResultDTO.empty("暂无相关会议，可进入感兴趣的会议详情页订阅标签获取推送");
        }

        return buildResult(query, pageResult);
    }

    /**
     * 获取筛选选项枚举值
     */
    public FilterOptionsDTO getFilterOptions() {
        FilterOptionsDTO options = new FilterOptionsDTO();

        // 会议形式
        options.setFormatOptions(Arrays.asList(
                new FilterOptionsDTO.FilterOption(0, "ALL", "全部"),
                new FilterOptionsDTO.FilterOption(1, "ONLINE", "线上"),
                new FilterOptionsDTO.FilterOption(2, "OFFLINE", "线下"),
                new FilterOptionsDTO.FilterOption(3, "HYBRID", "线上+线下")
        ));

        // 会议类型
        options.setTypeOptions(Arrays.asList(
                new FilterOptionsDTO.FilterOption(0, "ALL", "全部"),
                new FilterOptionsDTO.FilterOption(1, "SUMMIT", "技术峰会"),
                new FilterOptionsDTO.FilterOption(2, "SALON", "技术沙龙"),
                new FilterOptionsDTO.FilterOption(3, "WORKSHOP", "技术研讨会")
        ));

        // 会议场景
        options.setSceneOptions(Arrays.asList(
                new FilterOptionsDTO.FilterOption(0, "ALL", "全部"),
                new FilterOptionsDTO.FilterOption(1, "DEVELOPER", "开发者会议"),
                new FilterOptionsDTO.FilterOption(2, "INDUSTRY", "产业会议"),
                new FilterOptionsDTO.FilterOption(3, "PRODUCT", "产品发布会议"),
                new FilterOptionsDTO.FilterOption(4, "REGIONAL", "区域营销会议"),
                new FilterOptionsDTO.FilterOption(5, "UNIVERSITY", "高校会议")
        ));

        // 召开时间
        options.setTimeRangeOptions(Arrays.asList(
                new FilterOptionsDTO.FilterOption(0,"ALL", "全部"),
                new FilterOptionsDTO.FilterOption(1,"THIS_WEEK", "本周"),
                new FilterOptionsDTO.FilterOption(2,"THIS_MONTH", "本月"),
                new FilterOptionsDTO.FilterOption(3,"NEXT_3_MONTHS", "未来三个月")
        ));

        return options;
    }

    /**
     * 构建查询结果（统一使用 card 卡片结构返回）
     */
    private MeetingListResultDTO<MeetingCardItemDTO> buildResult(MeetingListQueryDTO query, PageResult<Meeting> pageResult) {
        MeetingListResultDTO<MeetingCardItemDTO> result = new MeetingListResultDTO<>();
        result.setTotal(pageResult.getTotalElements());
        result.setPage(pageResult.getPage());
        result.setSize(pageResult.getSize());
        result.setTotalPages(pageResult.getTotalPages());
        result.setEmpty(false);

        List<Meeting> meetings = pageResult.getContent();
        // 会议与标签关联通过 t_meeting.tags 字段（逗号分隔），从各会议解析后批量查 t_tag
        Map<String, List<Tag>> tagsByMeetingId = buildTagsByMeeting(meetings);

        List<MeetingCardItemDTO> items = meetings.stream()
                .map(m -> toCardItemDTO(m, tagsByMeetingId.getOrDefault(m.getMeetingId(), Collections.emptyList())))
                .collect(Collectors.toList());
        result.setItems(items);

        return result;
    }

    /**
     * 从各会议的 t_meeting.tags 解析标签，得到 meetingId -> List<Tag>
     * tags 字段可能为 tagId 列表（如 "1,2,3"）或 tagName 列表（如 "前端,后端"）
     */
    private Map<String, List<Tag>> buildTagsByMeeting(List<Meeting> meetings) {
        // 收集所有数字ID（用于查询t_tag表）和所有非数字名称
        Set<Long> allTagIds = new HashSet<>();
        Set<String> allTagNames = new HashSet<>();
        Map<String, List<Object>> rawTagsByMeeting = new LinkedHashMap<>();

        for (Meeting m : meetings) {
            String tagsStr = m.getTags();
            if (tagsStr == null || tagsStr.trim().isEmpty()) {
                rawTagsByMeeting.put(m.getMeetingId(), Collections.emptyList());
                continue;
            }

            List<Object> rawTags = new ArrayList<>();
            for (String s : tagsStr.split(",")) {
                String trimmed = s.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                try {
                    Long id = Long.parseLong(trimmed);
                    allTagIds.add(id);
                    rawTags.add(id);
                } catch (NumberFormatException e) {
                    // 是标签名而非ID
                    allTagNames.add(trimmed);
                    rawTags.add(trimmed);
                }
            }
            rawTagsByMeeting.put(m.getMeetingId(), rawTags);
        }

        // 批量查询ID对应的Tag
        Map<Long, Tag> tagById = new HashMap<>();
        if (!allTagIds.isEmpty()) {
            List<Tag> foundTags = tagRepository.findByIds(new ArrayList<>(allTagIds));
            tagById = foundTags.stream().collect(Collectors.toMap(Tag::getId, t -> t, (a, b) -> a));
        }

        // 查询名称对应的Tag（构造虚拟Tag对象）
        Map<String, Tag> tagByName = new HashMap<>();
        if (!allTagNames.isEmpty()) {
            // 尝试从数据库查询这些名称对应的tag
            List<Tag> foundByName = tagRepository.findByTagNamesIn(new ArrayList<>(allTagNames));
            for (Tag tag : foundByName) {
                tagByName.put(tag.getTagName(), tag);
            }
            // 对于数据库中不存在的标签名，构造虚拟Tag
            for (String name : allTagNames) {
                if (!tagByName.containsKey(name)) {
                    Tag virtualTag = new Tag();
                    virtualTag.setId(null);
                    virtualTag.setTagName(name);
                    tagByName.put(name, virtualTag);
                }
            }
        }

        // 组装结果
        Map<String, List<Tag>> tagsByMeetingId = new LinkedHashMap<>();
        for (Meeting m : meetings) {
            List<Object> rawTags = rawTagsByMeeting.getOrDefault(m.getMeetingId(), Collections.emptyList());
            List<Tag> tags = new ArrayList<>();
            for (Object raw : rawTags) {
                if (raw instanceof Long) {
                    Tag tag = tagById.get((Long) raw);
                    if (tag != null) {
                        tags.add(tag);
                    }
                } else if (raw instanceof String) {
                    Tag tag = tagByName.get((String) raw);
                    if (tag != null) {
                        tags.add(tag);
                    }
                }
            }
            tagsByMeetingId.put(m.getMeetingId(), tags);
        }
        return tagsByMeetingId;
    }

    /**
     * 转换为卡片视图DTO（含标签名称：由 tagId 批量查 tag 表得到 name）
     */
    private MeetingCardItemDTO toCardItemDTO(Meeting meeting, List<Tag> tags) {
        MeetingCardItemDTO dto = new MeetingCardItemDTO();
        dto.setId(meeting.getId());
        dto.setMeetingId(meeting.getMeetingId());
        dto.setTitle(meeting.getTitle());
        dto.setDescription(meeting.getDescription());
        dto.setCoverImage(meeting.getCoverImage() != null ? meeting.getCoverImage() : meeting.getPosterUrl());

        // 主办方：优先 organizerName，兼容旧字段 organizer
        dto.setOrganizerId(meeting.getOrganizerId());
        dto.setOrganizerName(meeting.getOrganizerName() != null ? meeting.getOrganizerName() : meeting.getOrganizer());
        dto.setOrganizerAvatar(meeting.getOrganizerAvatar());

        // 标签：由 tagId 查出的 Tag 转为 TagDTO（id + name + category）
        dto.setTags(toTagDTOList(tags));

        // 状态
        if (meeting.getStatus() != null) {
            dto.setStatusId(meeting.getStatus().getCode());
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

        // 形式（DB 可能存 code 字符串 "1"/"2"/"3"，MeetingFormat.of 已支持）
        if (meeting.getFormat() != null) {
            dto.setFormat(meeting.getFormat().name());
            dto.setFormatId(meeting.getFormat().getCode());
            dto.setFormatDisplay(meeting.getFormat().getDisplayName());
        }

        // 类型
        if (meeting.getMeetingType() != null) {
            dto.setMeetingType(meeting.getMeetingType().name());
            dto.setMeetingTypeId(meeting.getMeetingType().getCode());
            dto.setMeetingTypeDisplay(meeting.getMeetingType().getDisplayName());
        }

        // 场景（DB 可能存 code 字符串 "1"~"5"，MeetingScene.of 已支持）
        MeetingScene sceneEnum = MeetingScene.of(meeting.getScene());
        if (sceneEnum != null) {
            dto.setScene(sceneEnum.name());
            dto.setSceneId(sceneEnum.getCode());
            dto.setSceneDisplay(sceneEnum.getDisplayName());
        } else {
            dto.setScene(meeting.getScene());
        }

        // 热度
        dto.setHotScore(meeting.getHotScore());
        dto.setHotScoreDisplay(formatHotScore(meeting.getHotScore()));

        // 报名进度
        dto.setCurrentParticipants(meeting.getCurrentParticipants());
        dto.setMaxParticipants(meeting.getMaxParticipants());
        dto.setParticipantsDisplay(meeting.getParticipantsDisplay());

        // 地点：直接使用已存储的 cityName
        String cityName = meeting.getCityName();
        dto.setCityName(cityName);
        dto.setVenue(cityName != null ? cityName : meeting.getVenue());

        // 发布时间
        dto.setPublishTime(meeting.getPublishTime());

        return dto;
    }

    /**
     * Tag 实体转 TagDTO（id、name、category）
     */
    private List<TagDTO> toTagDTOList(List<Tag> tags) {
        if (tags == null || tags.isEmpty()) {
            return Collections.emptyList();
        }
        List<TagDTO> list = new ArrayList<>(tags.size());
        for (Tag tag : tags) {
            String category = tag.getTagCategory() != null ? tag.getTagCategory().name() : null;
            list.add(new TagDTO(tag.getId(), tag.getTagName(), category));
        }
        return list;
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
        MeetingType mt = MeetingType.of(type);
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
     * 通过城市编码反查城市名称
     */
    private String resolveCityName(String cityCode) {
        if (cityCode == null || cityCode.isEmpty()) {
            return null;
        }
        List<DictionaryDTO.RegionOption> regions = dictionaryUseCase.getRegionOptions();
        for (DictionaryDTO.RegionOption region : regions) {
            for (DictionaryDTO.CityOption city : region.getCities()) {
                if (cityCode.equals(city.getCityCode())) {
                    return city.getCityName();
                }
            }
        }
        return null;
    }

}
