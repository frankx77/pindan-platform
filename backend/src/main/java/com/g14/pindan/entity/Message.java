package com.g14.pindan.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息实体
 */
@Data
public class Message {

    private Integer id;
    private Integer userId;

    /** APPLY_NEW(团长新申请提醒) / JOIN_SUCCESS(申请成功) / JOIN_FAIL(申请失败) */
    private String type;

    private String title;
    private String content;

    /** 关联拼单ID（APPLY_NEW/JOIN_SUCCESS 可点击跳转；JOIN_FAIL 为空） */
    private Integer grouponId;

    /** 0=未读 1=已读 */
    private Integer isRead;

    private LocalDateTime createdAt;
}
