package com.csdn.meeting.infrastructure.mapper;

import com.csdn.meeting.domain.entity.*;
import com.csdn.meeting.infrastructure.po.MeetingAgendaItemPO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Converts between domain agenda tree (ScheduleDay->Session->SubVenue->Topic)
 * and flat MeetingAgendaItemPO list.
 */
@Component
public class AgendaTreeConverter {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Convert flat PO list to ScheduleDay tree.
     */
    public List<ScheduleDay> toScheduleDays(List<MeetingAgendaItemPO> items) {
        if (items == null || items.isEmpty()) return Collections.emptyList();

        List<MeetingAgendaItemPO> level1 = items.stream()
                .filter(p -> p.getLevel() != null && p.getLevel() == 1)
                .sorted(Comparator.comparing(p -> p.getSortOrder() != null ? p.getSortOrder() : 0))
                .collect(Collectors.toList());

        List<ScheduleDay> result = new ArrayList<>();
        for (MeetingAgendaItemPO dayPo : level1) {
            Map<String, Object> extra = parseExtra(dayPo.getExtra());
            LocalDate scheduleDate = parseDate((String) extra.get("schedule_date"));
            String dayLabel = (String) extra.getOrDefault("day_label", dayPo.getTitle());
            if (scheduleDate == null) continue;

            List<MeetingAgendaItemPO> sessionItems = items.stream()
                    .filter(p -> Objects.equals(p.getParentId(), dayPo.getId()))
                    .sorted(Comparator.comparing(p -> p.getSortOrder() != null ? p.getSortOrder() : 0))
                    .collect(Collectors.toList());

            List<Session> sessions = new ArrayList<>();
            for (MeetingAgendaItemPO sessionPo : sessionItems) {
                Map<String, Object> sExtra = parseExtra(sessionPo.getExtra());
                String sessionName = (String) sExtra.getOrDefault("session_name", sessionPo.getTitle());
                LocalTime startTime = parseTime((String) sExtra.get("start_time"));
                LocalTime endTime = parseTime((String) sExtra.get("end_time"));
                if (startTime == null) startTime = LocalTime.MIN;
                if (endTime == null) endTime = LocalTime.MAX;

                List<MeetingAgendaItemPO> subVenueItems = items.stream()
                        .filter(p -> Objects.equals(p.getParentId(), sessionPo.getId()))
                        .sorted(Comparator.comparing(p -> p.getSortOrder() != null ? p.getSortOrder() : 0))
                        .collect(Collectors.toList());

                List<SubVenue> subVenues = new ArrayList<>();
                for (MeetingAgendaItemPO svPo : subVenueItems) {
                    Map<String, Object> svExtra = parseExtra(svPo.getExtra());
                    String subVenueName = (String) svExtra.getOrDefault("sub_venue_name", svPo.getTitle());

                    List<MeetingAgendaItemPO> topicItems = items.stream()
                            .filter(p -> Objects.equals(p.getParentId(), svPo.getId()))
                            .sorted(Comparator.comparing(p -> p.getSortOrder() != null ? p.getSortOrder() : 0))
                            .collect(Collectors.toList());

                    @SuppressWarnings("unchecked")
                    List<Topic> topics = topicItems.stream()
                            .map(tPo -> {
                                Map<String, Object> tExtra = parseExtra(tPo.getExtra());
                                List<String> guests = (List<String>) tExtra.getOrDefault("guests", Collections.emptyList());
                                if (guests == null) guests = Collections.emptyList();
                                String topicIntro = (String) tExtra.getOrDefault("topic_intro", "");
                                String involvedProducts = (String) tExtra.getOrDefault("involved_products", "");
                                return new Topic(tPo.getTitle(), topicIntro, involvedProducts, guests);
                            })
                            .collect(Collectors.toList());
                    subVenues.add(new SubVenue(subVenueName, topics));
                }
                sessions.add(new Session(sessionName, startTime, endTime, subVenues));
            }
            result.add(new ScheduleDay(scheduleDate, dayLabel, sessions));
        }
        return result;
    }

    /**
     * Convert ScheduleDay tree to flat PO list in DFS order (parent before children).
     * parentIndex = index of parent in returned list, -1 for roots.
     */
    public List<AgendaItemData> toAgendaItemDataList(Long meetingId, List<ScheduleDay> scheduleDays) {
        if (meetingId == null || scheduleDays == null) return Collections.emptyList();

        List<AgendaItemData> result = new ArrayList<>();
        int dayOrder = 0;
        for (ScheduleDay day : scheduleDays) {
            int dayIdx = result.size();
            result.add(new AgendaItemData(1, -1, day.getDayLabel(), dayOrder++,
                    toJson(Map.of("schedule_date", day.getScheduleDate().toString(),
                            "day_label", day.getDayLabel() != null ? day.getDayLabel() : ""))));

            int sessionOrder = 0;
            for (Session session : day.getSessions()) {
                int sessionIdx = result.size();
                result.add(new AgendaItemData(2, dayIdx, session.getSessionName(), sessionOrder++,
                        toJson(Map.of("start_time", session.getStartTime().toString(),
                                "end_time", session.getEndTime().toString(),
                                "session_name", session.getSessionName() != null ? session.getSessionName() : ""))));

                int svOrder = 0;
                for (SubVenue sv : session.getSubVenues()) {
                    int svIdx = result.size();
                    result.add(new AgendaItemData(3, sessionIdx, sv.getSubVenueName(), svOrder++,
                            toJson(Map.of("sub_venue_name", sv.getSubVenueName() != null ? sv.getSubVenueName() : ""))));

                    int topicOrder = 0;
                    for (Topic topic : sv.getTopics()) {
                        Map<String, Object> tExtra = new HashMap<>();
                        tExtra.put("guests", topic.getGuests());
                        tExtra.put("topic_intro", topic.getTopicIntro() != null ? topic.getTopicIntro() : "");
                        tExtra.put("involved_products", topic.getInvolvedProducts() != null ? topic.getInvolvedProducts() : "");
                        result.add(new AgendaItemData(4, svIdx, topic.getTitle(), topicOrder++, toJson(tExtra)));
                    }
                }
            }
        }
        return result;
    }

    public MeetingAgendaItemPO toPO(AgendaItemData data, Long meetingId, Long parentId) {
        MeetingAgendaItemPO po = new MeetingAgendaItemPO();
        po.setMeetingId(meetingId);
        po.setParentId(parentId);
        po.setLevel(data.level);
        po.setTitle(data.title);
        po.setSortOrder(data.sortOrder);
        po.setExtra(data.extra);
        return po;
    }

    public static class AgendaItemData {
        public final int level;
        public final int parentIndex;
        public final String title;
        public final int sortOrder;
        public final String extra;

        public AgendaItemData(int level, int parentIndex, String title, int sortOrder, String extra) {
            this.level = level;
            this.parentIndex = parentIndex;
            this.title = title;
            this.sortOrder = sortOrder;
            this.extra = extra;
        }
    }

    private Map<String, Object> parseExtra(String json) {
        if (json == null || json.isEmpty()) return Collections.emptyMap();
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            return Collections.emptyMap();
        }
    }

    private String toJson(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.isEmpty()) return null;
        try {
            return LocalDate.parse(s);
        } catch (Exception e) {
            return null;
        }
    }

    private LocalTime parseTime(String s) {
        if (s == null || s.isEmpty()) return null;
        try {
            return LocalTime.parse(s);
        } catch (Exception e) {
            return null;
        }
    }
}
