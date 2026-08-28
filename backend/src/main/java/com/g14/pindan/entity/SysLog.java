package com.g14.pindan.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员操作日志实体
 */
@Data
public class SysLog {

    private Integer id;
    private Integer adminId;

    /** CANCEL_GROUPON / BAN_USER */
    private String action;

    private Integer targetId;
    private String detail;
    private LocalDateTime createdAt;
}
