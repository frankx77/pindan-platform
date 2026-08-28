package com.g14.pindan.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员视角的拼单列表项：拼单字段 + 团长信息
 */
@Data
public class AdminGrouponVO {

    private Integer id;
    private Integer publisherId;
    private String publisherNickname;
    private String publisherUsername;
    private String title;
    private String categoryTag;
    private String description;
    private Integer targetCount;
    private Integer currentCount;
    private LocalDateTime deadline;
    private Integer status;
    private LocalDateTime createdAt;
}
