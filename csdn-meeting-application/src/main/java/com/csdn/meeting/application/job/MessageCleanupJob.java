package com.csdn.meeting.application.job;

import com.csdn.meeting.domain.repository.UserMessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息清理定时任务
 * 定期清理过期的已读消息（保留90天）
 */
@Slf4j
@Component
public class MessageCleanupJob {

    private final UserMessageRepository messageRepository;

    // 消息保留天数（90天）
    private static final int RETENTION_DAYS = 90;

    // 每批处理数量
    private static final int BATCH_SIZE = 1000;

    public MessageCleanupJob(UserMessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    /**
     * 每天凌晨2点执行消息清理任务
     * 清理90天前的已读消息
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupExpiredMessages() {
        log.info("[消息清理任务] 开始执行...");

        try {
            // 计算截止日期（90天前）
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(RETENTION_DAYS);
            log.info("[消息清理任务] 清理{}天前的已读消息, 截止日期={}", RETENTION_DAYS, cutoffDate);

            // 执行清理
            int deletedCount = messageRepository.cleanupExpiredMessages(cutoffDate);

            log.info("[消息清理任务] 执行完成: 删除{}条过期消息", deletedCount);

        } catch (Exception e) {
            log.error("[消息清理任务] 执行失败", e);
        }
    }

    /**
     * 每小时检查一次过期消息数量（仅记录日志，不执行清理）
     * 用于监控过期消息数量，评估清理压力
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void checkExpiredMessageCount() {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(RETENTION_DAYS);

            // 查询过期消息ID列表（只查询前1000条，用于估算）
            List<String> expiredIds = messageRepository.findExpiredMessageIds(cutoffDate, 1000);
            int count = expiredIds.size();

            if (count >= 1000) {
                log.warn("[消息清理监控] 过期消息数量超过{}条，建议提前执行清理", count);
            } else if (count > 0) {
                log.info("[消息清理监控] 当前有{}条过期消息待清理", count);
            }

        } catch (Exception e) {
            log.error("[消息清理监控] 检查失败", e);
        }
    }

    /**
     * 手动触发消息清理（供管理员接口调用）
     *
     * @param retentionDays 保留天数（覆盖默认值）
     * @return 删除的消息数量
     */
    public int cleanupManually(int retentionDays) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(retentionDays);
        log.info("[消息清理-手动触发] 清理{}天前的已读消息, 截止日期={}", retentionDays, cutoffDate);

        return messageRepository.cleanupExpiredMessages(cutoffDate);
    }
}
