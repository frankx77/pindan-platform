-- ============================================
-- 校园拼单/组队平台 建库建表脚本
-- 数据库：pindan（utf8mb4）
-- 版本：阶段1 基础结构（不含演示数据，演示数据在阶段5补充）
-- ============================================

CREATE DATABASE IF NOT EXISTS pindan DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE pindan;

-- ---------- 表1：user 用户表 ----------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id`         INT          NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`   VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`   VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
    `nickname`   VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
    `avatar`     VARCHAR(200) DEFAULT NULL COMMENT '头像URL',
    `phone`      VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `wechat`     VARCHAR(50)  NOT NULL COMMENT '微信号（注册必填，拼团成功后互相可见）',
    `role`       TINYINT      NOT NULL DEFAULT 1 COMMENT '角色：1=普通用户 2=管理员',
    `status`     TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1=正常 0=禁用',
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ---------- 表2：groupon 拼单表 ----------
DROP TABLE IF EXISTS `groupon`;
CREATE TABLE `groupon` (
    `id`            INT          NOT NULL AUTO_INCREMENT COMMENT '拼单ID',
    `publisher_id`  INT          NOT NULL COMMENT '发起人（团长）ID',
    `title`         VARCHAR(100) NOT NULL COMMENT '标题',
    `category_tag`  VARCHAR(50)  NOT NULL DEFAULT '其他' COMMENT '分类标签（枚举label）',
    `description`   TEXT         COMMENT '详情描述',
    `target_count`  INT          NOT NULL COMMENT '目标人数（含团长）',
    `current_count` INT          NOT NULL DEFAULT 1 COMMENT '当前人数（含团长）',
    `deadline`      DATETIME     COMMENT '截止时间',
    `status`        TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0招募中 1已成团 2已结束 3已取消',
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    PRIMARY KEY (`id`),
    KEY `idx_publisher` (`publisher_id`),
    KEY `idx_status` (`status`),
    KEY `idx_category` (`category_tag`),
    CONSTRAINT `fk_groupon_user` FOREIGN KEY (`publisher_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='拼单表';

-- ---------- 表3：join_request 申请记录表 ----------
DROP TABLE IF EXISTS `join_request`;
CREATE TABLE `join_request` (
    `id`         INT      NOT NULL AUTO_INCREMENT COMMENT '申请ID',
    `groupon_id` INT      NOT NULL COMMENT '拼单ID',
    `user_id`    INT      NOT NULL COMMENT '申请人ID',
    `status`     TINYINT  NOT NULL DEFAULT 0 COMMENT '状态：0待确认 1已加入 2已拒绝 3已退出',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    `updated_at` DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '状态更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_groupon` (`groupon_id`),
    KEY `idx_user` (`user_id`),
    UNIQUE KEY `uk_groupon_user` (`groupon_id`, `user_id`),
    CONSTRAINT `fk_jr_groupon` FOREIGN KEY (`groupon_id`) REFERENCES `groupon` (`id`),
    CONSTRAINT `fk_jr_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='申请记录表';

-- ---------- 表4：sys_log 管理员操作日志表 ----------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log` (
    `id`         INT          NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `admin_id`   INT          NOT NULL COMMENT '操作人ID',
    `action`     VARCHAR(50)  NOT NULL COMMENT '操作类型：CANCEL_GROUPON / BAN_USER',
    `target_id`  INT          NOT NULL COMMENT '被操作对象ID',
    `detail`     VARCHAR(200) DEFAULT NULL COMMENT '详情',
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`id`),
    KEY `idx_admin` (`admin_id`),
    KEY `idx_action` (`action`),
    CONSTRAINT `fk_log_admin` FOREIGN KEY (`admin_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员操作日志表';

-- ---------- 表5：message 消息表 ----------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
    `id`         INT          NOT NULL AUTO_INCREMENT COMMENT '消息ID',
    `user_id`    INT          NOT NULL COMMENT '接收者ID',
    `type`       VARCHAR(20)  NOT NULL COMMENT '类型：APPLY_NEW团长新申请提醒 / JOIN_SUCCESS申请成功 / JOIN_FAIL申请失败',
    `title`      VARCHAR(100) NOT NULL COMMENT '标题',
    `content`    VARCHAR(500) DEFAULT NULL COMMENT '内容',
    `groupon_id` INT          DEFAULT NULL COMMENT '关联拼单ID（可点击跳转用，失败消息为空）',
    `is_read`    TINYINT      NOT NULL DEFAULT 0 COMMENT '0=未读 1=已读',
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    PRIMARY KEY (`id`),
    KEY `idx_user` (`user_id`),
    KEY `idx_user_read` (`user_id`, `is_read`),
    CONSTRAINT `fk_msg_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息表';

-- ============================================
-- 演示数据说明（阶段5）
-- 本文件只建库建表（空库）。完整演示数据见同目录 demo-data.sql：
--   8 个用户（含 1 个管理员 admin/admin123）+ 14 个拼单（四种状态全覆盖）
--   + 16 条申请记录 + 消息 + 管理员日志
-- 全新环境初始化：先执行本文件，再执行 demo-data.sql（幂等，可重复执行）。
-- ============================================
