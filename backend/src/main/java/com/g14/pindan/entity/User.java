package com.g14.pindan.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
public class User {

    private Integer id;
    private String username;

    /** BCrypt 加密存储，序列化时隐藏 */
    @JsonIgnore
    private String password;

    private String nickname;
    private String avatar;
    private String phone;

    /** 微信号（注册必填；拼团成功后团长/成员互相可见，未成团时接口不返回） */
    private String wechat;

    /** 1=普通用户 2=管理员 */
    private Integer role;

    /** 1=正常 0=禁用 */
    private Integer status;

    private LocalDateTime createdAt;
}
