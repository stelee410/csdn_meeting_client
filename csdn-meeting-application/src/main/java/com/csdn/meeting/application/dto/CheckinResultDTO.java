package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 签到结果DTO
 */
@Data
@Schema(description = "签到结果")
public class CheckinResultDTO {

    @Schema(description = "签到是否成功", example = "true")
    private boolean success;

    @Schema(description = "状态码：success/duplicate/not_registered/not_approved/invalid_code/expired", example = "success")
    private String code;

    @Schema(description = "提示消息", example = "签到成功！欢迎参加本次会议")
    private String message;

    @Schema(description = "会议ID")
    private String meetingId;

    @Schema(description = "会议标题", example = "2024 AI技术创新峰会")
    private String meetingTitle;

    @Schema(description = "签到时间")
    private LocalDateTime checkinTime;

    @Schema(description = "用户姓名")
    private String userName;

    @Schema(description = "座位号（如有）", example = "A区-12排-08号")
    private String seatNumber;

    @Schema(description = "入场指引", example = "请从3号门入场，前往A区就座")
    private String entranceGuide;

    @Schema(description = "之前签到时间（重复签到时返回）")
    private LocalDateTime previousCheckinTime;

    public static CheckinResultDTO success(String meetingId, String meetingTitle, 
                                           String userName, LocalDateTime checkinTime) {
        CheckinResultDTO result = new CheckinResultDTO();
        result.setSuccess(true);
        result.setCode("success");
        result.setMessage("签到成功！欢迎参加本次会议");
        result.setMeetingId(meetingId);
        result.setMeetingTitle(meetingTitle);
        result.setUserName(userName);
        result.setCheckinTime(checkinTime);
        return result;
    }

    public static CheckinResultDTO duplicate(String meetingId, String meetingTitle,
                                             String userName, LocalDateTime previousTime) {
        CheckinResultDTO result = new CheckinResultDTO();
        result.setSuccess(false);
        result.setCode("duplicate");
        result.setMessage("您已完成签到，无需重复操作");
        result.setMeetingId(meetingId);
        result.setMeetingTitle(meetingTitle);
        result.setUserName(userName);
        result.setPreviousCheckinTime(previousTime);
        return result;
    }

    public static CheckinResultDTO notRegistered(String meetingId, String meetingTitle) {
        CheckinResultDTO result = new CheckinResultDTO();
        result.setSuccess(false);
        result.setCode("not_registered");
        result.setMessage("未查询到您的报名记录，请先报名或联系现场工作人员");
        result.setMeetingId(meetingId);
        result.setMeetingTitle(meetingTitle);
        return result;
    }

    public static CheckinResultDTO notApproved(String meetingId, String meetingTitle) {
        CheckinResultDTO result = new CheckinResultDTO();
        result.setSuccess(false);
        result.setCode("not_approved");
        result.setMessage("您的报名尚未通过审核，请联系主办方");
        result.setMeetingId(meetingId);
        result.setMeetingTitle(meetingTitle);
        return result;
    }

    public static CheckinResultDTO invalidCode(String meetingId) {
        CheckinResultDTO result = new CheckinResultDTO();
        result.setSuccess(false);
        result.setCode("invalid_code");
        result.setMessage("无效的签到码，请确认二维码是否正确");
        result.setMeetingId(meetingId);
        return result;
    }

    public static CheckinResultDTO expired(String meetingId, String meetingTitle) {
        CheckinResultDTO result = new CheckinResultDTO();
        result.setSuccess(false);
        result.setCode("expired");
        result.setMessage("签到通道已关闭");
        result.setMeetingId(meetingId);
        result.setMeetingTitle(meetingTitle);
        return result;
    }
}
