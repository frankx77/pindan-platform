package com.g14.pindan.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 拼单成员视图（联表 user 信息）
 */
@Data
public class MemberVO {

    private Integer id;
    private Integer grouponId;
    private Integer userId;
    private Integer status;
    private LocalDateTime createdAt;
    private String username;
    private String nickname;
    private String avatar;

    /** 微信号：仅拼单已成团时由接口填充，否则为 null（保密） */
    private String wechat;
}
