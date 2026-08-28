package com.g14.pindan.task;

import com.g14.pindan.mapper.GrouponMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务：扫描截止超时的拼单
 */
@Component
public class GrouponTimeoutTask {

    private static final Logger log = LoggerFactory.getLogger(GrouponTimeoutTask.class);

    private final GrouponMapper grouponMapper;

    public GrouponTimeoutTask(GrouponMapper grouponMapper) {
        this.grouponMapper = grouponMapper;
    }

    /**
     * 每 5 分钟扫描一次：招募中且已过截止时间 → 已结束(2)
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void closeExpiredGroupons() {
        int count = grouponMapper.closeExpired();
        if (count > 0) {
            log.info("定时任务：已将 {} 个超时拼单置为已结束", count);
        }
    }
}
