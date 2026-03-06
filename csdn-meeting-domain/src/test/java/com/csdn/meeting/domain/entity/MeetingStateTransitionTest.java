package com.csdn.meeting.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Meeting 状态流转")
class MeetingStateTransitionTest {

    private Meeting createMeetingWithStatus(Meeting.MeetingStatus status) {
        Meeting m = new Meeting();
        m.setMeetingId("M001");
        m.setTitle("Test Meeting");
        m.setStatus(status);
        return m;
    }

    @Nested
    @DisplayName("submit() DRAFT/REJECTED -> PENDING_REVIEW")
    class SubmitTests {

        @Test
        @DisplayName("DRAFT 状态下 submit 应转为 PENDING_REVIEW")
        void submit_fromDraft_succeeds() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.DRAFT);
            meeting.submit();
            assertEquals(Meeting.MeetingStatus.PENDING_REVIEW, meeting.getStatus());
        }

        @Test
        @DisplayName("REJECTED 状态下 submit 应转为 PENDING_REVIEW")
        void submit_fromRejected_succeeds() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.REJECTED);
            meeting.submit();
            assertEquals(Meeting.MeetingStatus.PENDING_REVIEW, meeting.getStatus());
        }

        @Test
        @DisplayName("PENDING_REVIEW 状态下 submit 应抛异常")
        void submit_fromPendingReview_throws() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.PENDING_REVIEW);
            assertThrows(IllegalStateException.class, meeting::submit);
        }

        @Test
        @DisplayName("PUBLISHED 状态下 submit 应抛异常")
        void submit_fromPublished_throws() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.PUBLISHED);
            assertThrows(IllegalStateException.class, meeting::submit);
        }

        @Test
        @DisplayName("IN_PROGRESS 状态下 submit 应抛异常")
        void submit_fromInProgress_throws() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.IN_PROGRESS);
            assertThrows(IllegalStateException.class, meeting::submit);
        }

        @Test
        @DisplayName("ENDED 状态下 submit 应抛异常")
        void submit_fromEnded_throws() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.ENDED);
            assertThrows(IllegalStateException.class, meeting::submit);
        }

        @Test
        @DisplayName("OFFLINE 状态下 submit 应抛异常")
        void submit_fromOffline_throws() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.OFFLINE);
            assertThrows(IllegalStateException.class, meeting::submit);
        }

        @Test
        @DisplayName("DELETED 状态下 submit 应抛异常")
        void submit_fromDeleted_throws() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.DELETED);
            assertThrows(IllegalStateException.class, meeting::submit);
        }
    }

    @Nested
    @DisplayName("withdraw() PENDING_REVIEW -> DRAFT")
    class WithdrawTests {

        @Test
        @DisplayName("PENDING_REVIEW 状态下 withdraw 应转为 DRAFT")
        void withdraw_fromPendingReview_succeeds() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.PENDING_REVIEW);
            meeting.withdraw();
            assertEquals(Meeting.MeetingStatus.DRAFT, meeting.getStatus());
        }

        @Test
        @DisplayName("DRAFT 状态下 withdraw 应抛异常")
        void withdraw_fromDraft_throws() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.DRAFT);
            assertThrows(IllegalStateException.class, meeting::withdraw);
        }

        @Test
        @DisplayName("REJECTED 状态下 withdraw 应抛异常")
        void withdraw_fromRejected_throws() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.REJECTED);
            assertThrows(IllegalStateException.class, meeting::withdraw);
        }

        @Test
        @DisplayName("PUBLISHED 状态下 withdraw 应抛异常")
        void withdraw_fromPublished_throws() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.PUBLISHED);
            assertThrows(IllegalStateException.class, meeting::withdraw);
        }

        @Test
        @DisplayName("IN_PROGRESS 状态下 withdraw 应抛异常")
        void withdraw_fromInProgress_throws() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.IN_PROGRESS);
            assertThrows(IllegalStateException.class, meeting::withdraw);
        }

        @Test
        @DisplayName("ENDED 状态下 withdraw 应抛异常")
        void withdraw_fromEnded_throws() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.ENDED);
            assertThrows(IllegalStateException.class, meeting::withdraw);
        }
    }

    @Nested
    @DisplayName("submit -> withdraw 往返")
    class SubmitWithdrawRoundtrip {

        @Test
        @DisplayName("DRAFT -> submit -> PENDING_REVIEW -> withdraw -> DRAFT")
        void roundtrip_draft_submit_withdraw() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.DRAFT);
            meeting.submit();
            assertEquals(Meeting.MeetingStatus.PENDING_REVIEW, meeting.getStatus());
            meeting.withdraw();
            assertEquals(Meeting.MeetingStatus.DRAFT, meeting.getStatus());
        }

        @Test
        @DisplayName("REJECTED -> submit -> PENDING_REVIEW -> withdraw -> DRAFT")
        void roundtrip_rejected_submit_withdraw() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.REJECTED);
            meeting.submit();
            assertEquals(Meeting.MeetingStatus.PENDING_REVIEW, meeting.getStatus());
            meeting.withdraw();
            assertEquals(Meeting.MeetingStatus.DRAFT, meeting.getStatus());
        }
    }

    @Nested
    @DisplayName("approve() PENDING_REVIEW -> PUBLISHED")
    class ApproveTests {

        @Test
        @DisplayName("PENDING_REVIEW 状态下 approve 应转为 PUBLISHED")
        void approve_fromPendingReview_succeeds() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.PENDING_REVIEW);
            meeting.approve();
            assertEquals(Meeting.MeetingStatus.PUBLISHED, meeting.getStatus());
        }

        @Test
        @DisplayName("DRAFT 状态下 approve 应抛异常")
        void approve_fromDraft_throws() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.DRAFT);
            assertThrows(IllegalStateException.class, meeting::approve);
        }

        @Test
        @DisplayName("PUBLISHED 状态下 approve 应抛异常")
        void approve_fromPublished_throws() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.PUBLISHED);
            assertThrows(IllegalStateException.class, meeting::approve);
        }
    }

    @Nested
    @DisplayName("reject(reason) PENDING_REVIEW -> REJECTED")
    class RejectTests {

        @Test
        @DisplayName("PENDING_REVIEW 状态下 reject 应转为 REJECTED 并设置 rejectReason")
        void reject_fromPendingReview_succeeds() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.PENDING_REVIEW);
            meeting.reject("内容不合规");
            assertEquals(Meeting.MeetingStatus.REJECTED, meeting.getStatus());
            assertEquals("内容不合规", meeting.getRejectReason());
        }

        @Test
        @DisplayName("reject 时 reason 为空应抛 IllegalArgumentException")
        void reject_withBlankReason_throws() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.PENDING_REVIEW);
            assertThrows(IllegalArgumentException.class, () -> meeting.reject(null));
            assertThrows(IllegalArgumentException.class, () -> meeting.reject(""));
            assertThrows(IllegalArgumentException.class, () -> meeting.reject("   "));
        }

        @Test
        @DisplayName("DRAFT 状态下 reject 应抛异常")
        void reject_fromDraft_throws() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.DRAFT);
            assertThrows(IllegalStateException.class, () -> meeting.reject("reason"));
        }
    }

    @Nested
    @DisplayName("resubmit() REJECTED -> PENDING_REVIEW")
    class ResubmitTests {

        @Test
        @DisplayName("REJECTED 状态下 resubmit 应转为 PENDING_REVIEW 并清空 rejectReason")
        void resubmit_fromRejected_succeeds() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.REJECTED);
            meeting.setRejectReason("需要修改");
            meeting.resubmit();
            assertEquals(Meeting.MeetingStatus.PENDING_REVIEW, meeting.getStatus());
            assertNull(meeting.getRejectReason());
        }

        @Test
        @DisplayName("DRAFT 状态下 resubmit 应抛异常")
        void resubmit_fromDraft_throws() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.DRAFT);
            assertThrows(IllegalStateException.class, meeting::resubmit);
        }

        @Test
        @DisplayName("PENDING_REVIEW 状态下 resubmit 应抛异常")
        void resubmit_fromPendingReview_throws() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.PENDING_REVIEW);
            assertThrows(IllegalStateException.class, meeting::resubmit);
        }
    }

    @Nested
    @DisplayName("takedown(reason) PUBLISHED/IN_PROGRESS -> OFFLINE")
    class TakedownTests {

        @Test
        @DisplayName("PUBLISHED 状态下 takedown 应转为 OFFLINE")
        void takedown_fromPublished_succeeds() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.PUBLISHED);
            meeting.takedown("主办方申请下架");
            assertEquals(Meeting.MeetingStatus.OFFLINE, meeting.getStatus());
            assertEquals("主办方申请下架", meeting.getTakedownReason());
        }

        @Test
        @DisplayName("IN_PROGRESS 状态下 takedown 应转为 OFFLINE")
        void takedown_fromInProgress_succeeds() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.IN_PROGRESS);
            meeting.takedown("紧急下架");
            assertEquals(Meeting.MeetingStatus.OFFLINE, meeting.getStatus());
        }

        @Test
        @DisplayName("takedown 时 reason 为空应抛 IllegalArgumentException")
        void takedown_withBlankReason_throws() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.PUBLISHED);
            assertThrows(IllegalArgumentException.class, () -> meeting.takedown(null));
            assertThrows(IllegalArgumentException.class, () -> meeting.takedown(""));
        }

        @Test
        @DisplayName("DRAFT 状态下 takedown 应抛异常")
        void takedown_fromDraft_throws() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.DRAFT);
            assertThrows(IllegalStateException.class, () -> meeting.takedown("reason"));
        }

        @Test
        @DisplayName("ENDED 状态下 takedown 应抛异常")
        void takedown_fromEnded_throws() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.ENDED);
            assertThrows(IllegalStateException.class, () -> meeting.takedown("reason"));
        }
    }

    @Nested
    @DisplayName("delete() DRAFT/ENDED/OFFLINE/REJECTED -> DELETED")
    class DeleteTests {

        @Test
        @DisplayName("DRAFT 状态下 delete 应转为 DELETED")
        void delete_fromDraft_succeeds() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.DRAFT);
            meeting.delete();
            assertEquals(Meeting.MeetingStatus.DELETED, meeting.getStatus());
        }

        @Test
        @DisplayName("ENDED 状态下 delete 应转为 DELETED")
        void delete_fromEnded_succeeds() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.ENDED);
            meeting.delete();
            assertEquals(Meeting.MeetingStatus.DELETED, meeting.getStatus());
        }

        @Test
        @DisplayName("OFFLINE 状态下 delete 应转为 DELETED")
        void delete_fromOffline_succeeds() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.OFFLINE);
            meeting.delete();
            assertEquals(Meeting.MeetingStatus.DELETED, meeting.getStatus());
        }

        @Test
        @DisplayName("REJECTED 状态下 delete 应转为 DELETED")
        void delete_fromRejected_succeeds() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.REJECTED);
            meeting.delete();
            assertEquals(Meeting.MeetingStatus.DELETED, meeting.getStatus());
        }

        @Test
        @DisplayName("PUBLISHED 状态下 delete 应抛异常")
        void delete_fromPublished_throws() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.PUBLISHED);
            assertThrows(IllegalStateException.class, meeting::delete);
        }

        @Test
        @DisplayName("IN_PROGRESS 状态下 delete 应抛异常")
        void delete_fromInProgress_throws() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.IN_PROGRESS);
            assertThrows(IllegalStateException.class, meeting::delete);
        }

        @Test
        @DisplayName("PENDING_REVIEW 状态下 delete 应抛异常")
        void delete_fromPendingReview_throws() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.PENDING_REVIEW);
            assertThrows(IllegalStateException.class, meeting::delete);
        }
    }

    @Nested
    @DisplayName("autoStart() PUBLISHED -> IN_PROGRESS")
    class AutoStartTests {

        @Test
        @DisplayName("PUBLISHED 状态下 autoStart 应转为 IN_PROGRESS")
        void autoStart_fromPublished_succeeds() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.PUBLISHED);
            meeting.autoStart();
            assertEquals(Meeting.MeetingStatus.IN_PROGRESS, meeting.getStatus());
        }

        @Test
        @DisplayName("DRAFT 状态下 autoStart 应抛异常")
        void autoStart_fromDraft_throws() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.DRAFT);
            assertThrows(IllegalStateException.class, meeting::autoStart);
        }

        @Test
        @DisplayName("IN_PROGRESS 状态下 autoStart 应抛异常")
        void autoStart_fromInProgress_throws() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.IN_PROGRESS);
            assertThrows(IllegalStateException.class, meeting::autoStart);
        }

        @Test
        @DisplayName("ENDED 状态下 autoStart 应抛异常")
        void autoStart_fromEnded_throws() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.ENDED);
            assertThrows(IllegalStateException.class, meeting::autoStart);
        }
    }

    @Nested
    @DisplayName("autoEnd() IN_PROGRESS -> ENDED")
    class AutoEndTests {

        @Test
        @DisplayName("IN_PROGRESS 状态下 autoEnd 应转为 ENDED")
        void autoEnd_fromInProgress_succeeds() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.IN_PROGRESS);
            meeting.autoEnd();
            assertEquals(Meeting.MeetingStatus.ENDED, meeting.getStatus());
        }

        @Test
        @DisplayName("PUBLISHED 状态下 autoEnd 应抛异常")
        void autoEnd_fromPublished_throws() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.PUBLISHED);
            assertThrows(IllegalStateException.class, meeting::autoEnd);
        }

        @Test
        @DisplayName("DRAFT 状态下 autoEnd 应抛异常")
        void autoEnd_fromDraft_throws() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.DRAFT);
            assertThrows(IllegalStateException.class, meeting::autoEnd);
        }

        @Test
        @DisplayName("ENDED 状态下 autoEnd 应抛异常")
        void autoEnd_fromEnded_throws() {
            Meeting meeting = createMeetingWithStatus(Meeting.MeetingStatus.ENDED);
            assertThrows(IllegalStateException.class, meeting::autoEnd);
        }
    }
}
