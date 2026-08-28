package com.g14.pindan.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 申请记录实体
 */
@Data
public class JoinRequest {

    private Integer id;
    private Integer grouponId;
    private Integer userId;

    /** 0待确认 1已加入 2已拒绝 3已退出 */
    private Integer status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
