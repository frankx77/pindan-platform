package com.g14.pindan.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员视角的操作日志列表项：日志字段 + 操作人用户名
 */
@Data
public class SysLogVO {

    private Integer id;
    private Integer adminId;
    private String adminUsername;
    private String action;
    private Integer targetId;
    private String detail;
    private LocalDateTime createdAt;
}
