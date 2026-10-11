-- ============================================
-- 校园拼单/组队平台 演示数据（阶段5）
-- 用法：先执行 init.sql 建库建表，再执行本文件灌入演示数据
-- 幂等：INSERT IGNORE，重复执行不会产生脏数据
-- 测试账号：admin/admin123（管理员）、zhangsan/123456、wangwu/abcdef、
--          lisi/123456、test1/123456、karina/123456
-- ============================================
USE pindan;

-- ---------- 演示用户（含 1 个管理员） ----------
INSERT IGNORE INTO `user` (id, username, password, nickname, avatar, phone, wechat, role, status, created_at) VALUES (1, 'zhangsan', '$2a$10$7YraBBySFowAnyn3SG7ezOlVCG7WSXFXc9pVcht9qUB1lKhs/klme', 'zhangsan', NULL, NULL, 'wx_zhangsan', 1, 1, '2026-08-25 04:29:19');
INSERT IGNORE INTO `user` (id, username, password, nickname, avatar, phone, wechat, role, status, created_at) VALUES (2, 'wangwu', '$2a$10$/yOW8kHFg2F9Ot0ZLW7rP.aVgzZ00yzRw5SOnURvCUQpFBlLSn16q', 'wangwu', NULL, NULL, 'wx_wangwu', 1, 1, '2026-08-25 04:29:50');
INSERT IGNORE INTO `user` (id, username, password, nickname, avatar, phone, wechat, role, status, created_at) VALUES (3, 'karina', '$2a$10$jGCKTUFZX8h9hLoHHFpQvuJw9M9kZuWIPo91Tio7uvQpzMGF0s4WK', 'karina', NULL, NULL, 'wx_karina', 1, 1, '2026-08-25 04:36:57');
INSERT IGNORE INTO `user` (id, username, password, nickname, avatar, phone, wechat, role, status, created_at) VALUES (4, 'lisi', '$2a$10$hXGL46ftKYn8piDuY/3YPO8dd1DJpMDWZbHZSBt7wI7zSbykxRL4a', 'lisi', NULL, NULL, 'wx_lisi', 1, 1, '2026-08-25 04:40:50');
INSERT IGNORE INTO `user` (id, username, password, nickname, avatar, phone, wechat, role, status, created_at) VALUES (5, 'test1', '$2a$10$hae5wZtKqeRWvreRBkT3gOdO.NEybVKHv6wyUqXTQtE8m7sdBnPTq', 'test1', NULL, NULL, 'wx_test1', 1, 1, '2026-08-25 06:00:31');
INSERT IGNORE INTO `user` (id, username, password, nickname, avatar, phone, wechat, role, status, created_at) VALUES (6, 'frank', '$2a$10$SZqwFB/kj7nsBq/ammxHnum7fkJuKs21iEHGghVQ7mCo.9ecvu97a', 'frank', NULL, NULL, '13812341234', 1, 1, '2026-08-25 06:19:28');
INSERT IGNORE INTO `user` (id, username, password, nickname, avatar, phone, wechat, role, status, created_at) VALUES (7, 'admin', '$2b$12$tS23U3KegWBZbfI2AYuzUuUYRwQa/CT55gwo2JLQcKu5tGNGakAtm', '平台管理员', NULL, NULL, 'wx_admin', 2, 1, '2026-08-26 04:29:43');
INSERT IGNORE INTO `user` (id, username, password, nickname, avatar, phone, wechat, role, status, created_at) VALUES (8, 'banme', '$2a$10$zNHNasgZvZUF60VvbF6X/..pqICdpFEMMDzFWFxN8L6vLngD1/JW.', 'banme', NULL, NULL, 'wx_banme', 1, 1, '2026-08-26 04:31:15');

-- ---------- 演示拼单（覆盖 招募中/已成团/已结束/已取消 四种状态） ----------
INSERT IGNORE INTO `groupon` (id, publisher_id, title, category_tag, description, target_count, current_count, deadline, status, created_at) VALUES (1, 1, '拼奶茶，3人成团', '奶茶', '起送价太高', 3, 3, DATE_ADD(NOW(), INTERVAL 15 DAY), 1, '2026-08-25 04:39:01');
INSERT IGNORE INTO `groupon` (id, publisher_id, title, category_tag, description, target_count, current_count, deadline, status, created_at) VALUES (2, 1, '拼车回家', '拼车', NULL, 4, 1, DATE_ADD(NOW(), INTERVAL 20 DAY), 3, '2026-08-25 04:39:01');
INSERT IGNORE INTO `groupon` (id, publisher_id, title, category_tag, description, target_count, current_count, deadline, status, created_at) VALUES (3, 4, '周末看电影，2人拼', '电影票', NULL, 2, 2, DATE_ADD(NOW(), INTERVAL 15 DAY), 1, '2026-08-25 04:40:50');
INSERT IGNORE INTO `groupon` (id, publisher_id, title, category_tag, description, target_count, current_count, deadline, status, created_at) VALUES (4, 3, '拼车到南京南站', '拼车', '行李不多', 2, 2, DATE_ADD(NOW(), INTERVAL 15 DAY), 1, '2026-08-25 04:58:24');
INSERT IGNORE INTO `groupon` (id, publisher_id, title, category_tag, description, target_count, current_count, deadline, status, created_at) VALUES (5, 2, '拼水果，2人团', '水果', NULL, 2, 2, DATE_ADD(NOW(), INTERVAL 15 DAY), 1, '2026-08-25 05:05:44');
INSERT IGNORE INTO `groupon` (id, publisher_id, title, category_tag, description, target_count, current_count, deadline, status, created_at) VALUES (6, 1, '拼电影，4人团', '电影票', NULL, 4, 1, DATE_ADD(NOW(), INTERVAL 30 DAY), 0, '2026-08-25 05:06:37');
INSERT IGNORE INTO `groupon` (id, publisher_id, title, category_tag, description, target_count, current_count, deadline, status, created_at) VALUES (7, 2, '拼早餐，3人团', '其他', NULL, 3, 1, DATE_ADD(NOW(), INTERVAL 30 DAY), 0, '2026-08-25 05:06:38');
INSERT IGNORE INTO `groupon` (id, publisher_id, title, category_tag, description, target_count, current_count, deadline, status, created_at) VALUES (8, 1, '拼电影，4人团', '电影票', NULL, 4, 1, DATE_ADD(NOW(), INTERVAL 20 DAY), 3, '2026-08-25 05:06:50');
INSERT IGNORE INTO `groupon` (id, publisher_id, title, category_tag, description, target_count, current_count, deadline, status, created_at) VALUES (9, 2, '拼早餐，3人团', '其他', NULL, 3, 1, DATE_ADD(NOW(), INTERVAL 20 DAY), 3, '2026-08-25 05:06:51');
INSERT IGNORE INTO `groupon` (id, publisher_id, title, category_tag, description, target_count, current_count, deadline, status, created_at) VALUES (10, 2, '拼超市，2人团', '其他', NULL, 2, 2, DATE_SUB(NOW(), INTERVAL 7 DAY), 2, '2026-08-25 05:07:00');
INSERT IGNORE INTO `groupon` (id, publisher_id, title, category_tag, description, target_count, current_count, deadline, status, created_at) VALUES (11, 1, '拼水果，3人团', '水果', NULL, 3, 2, DATE_ADD(NOW(), INTERVAL 20 DAY), 3, '2026-08-25 05:08:41');
INSERT IGNORE INTO `groupon` (id, publisher_id, title, category_tag, description, target_count, current_count, deadline, status, created_at) VALUES (12, 1, '拼晚餐，3人团', '其他', NULL, 3, 2, DATE_ADD(NOW(), INTERVAL 20 DAY), 3, '2026-08-25 06:40:48');
INSERT IGNORE INTO `groupon` (id, publisher_id, title, category_tag, description, target_count, current_count, deadline, status, created_at) VALUES (13, 5, '拼游戏，2人团', '其他', NULL, 2, 2, DATE_ADD(NOW(), INTERVAL 15 DAY), 1, '2026-08-25 06:44:21');
INSERT IGNORE INTO `groupon` (id, publisher_id, title, category_tag, description, target_count, current_count, deadline, status, created_at) VALUES (14, 5, '拼雪糕，3人团', '其他', NULL, 3, 2, DATE_ADD(NOW(), INTERVAL 20 DAY), 3, '2026-08-25 06:44:57');

-- ---------- 演示申请记录（待确认/已加入/已拒绝/已退出 全覆盖） ----------
INSERT IGNORE INTO `join_request` (id, groupon_id, user_id, status, created_at, updated_at) VALUES (1, 1, 4, 1, '2026-08-25 05:05:26', '2026-08-25 05:05:34');
INSERT IGNORE INTO `join_request` (id, groupon_id, user_id, status, created_at, updated_at) VALUES (2, 1, 2, 1, '2026-08-25 05:05:34', '2026-08-25 05:05:34');
INSERT IGNORE INTO `join_request` (id, groupon_id, user_id, status, created_at, updated_at) VALUES (3, 4, 4, 1, '2026-08-25 05:05:45', '2026-08-25 05:23:19');
INSERT IGNORE INTO `join_request` (id, groupon_id, user_id, status, created_at, updated_at) VALUES (4, 5, 4, 0, '2026-08-25 05:06:37', NULL);
INSERT IGNORE INTO `join_request` (id, groupon_id, user_id, status, created_at, updated_at) VALUES (5, 8, 4, 3, '2026-08-25 05:06:50', '2026-08-25 05:06:51');
INSERT IGNORE INTO `join_request` (id, groupon_id, user_id, status, created_at, updated_at) VALUES (6, 9, 4, 2, '2026-08-25 05:06:51', '2026-08-25 05:06:51');
INSERT IGNORE INTO `join_request` (id, groupon_id, user_id, status, created_at, updated_at) VALUES (7, 10, 4, 1, '2026-08-25 05:07:00', '2026-08-25 05:07:00');
INSERT IGNORE INTO `join_request` (id, groupon_id, user_id, status, created_at, updated_at) VALUES (8, 11, 4, 1, '2026-08-25 05:12:18', '2026-08-25 05:14:41');
INSERT IGNORE INTO `join_request` (id, groupon_id, user_id, status, created_at, updated_at) VALUES (9, 4, 1, 0, '2026-08-25 05:18:50', NULL);
INSERT IGNORE INTO `join_request` (id, groupon_id, user_id, status, created_at, updated_at) VALUES (10, 5, 5, 1, '2026-08-25 06:00:38', '2026-08-25 06:00:44');
INSERT IGNORE INTO `join_request` (id, groupon_id, user_id, status, created_at, updated_at) VALUES (11, 3, 6, 1, '2026-08-25 06:19:41', '2026-08-25 06:19:59');
INSERT IGNORE INTO `join_request` (id, groupon_id, user_id, status, created_at, updated_at) VALUES (12, 12, 4, 2, '2026-08-25 06:40:49', '2026-08-25 06:40:49');
INSERT IGNORE INTO `join_request` (id, groupon_id, user_id, status, created_at, updated_at) VALUES (13, 12, 2, 1, '2026-08-25 06:41:08', '2026-08-25 06:41:09');
INSERT IGNORE INTO `join_request` (id, groupon_id, user_id, status, created_at, updated_at) VALUES (14, 13, 2, 1, '2026-08-25 06:44:22', '2026-08-25 09:25:13');
INSERT IGNORE INTO `join_request` (id, groupon_id, user_id, status, created_at, updated_at) VALUES (15, 14, 2, 2, '2026-08-25 06:44:58', '2026-08-25 06:44:58');
INSERT IGNORE INTO `join_request` (id, groupon_id, user_id, status, created_at, updated_at) VALUES (16, 14, 3, 1, '2026-08-25 09:24:59', '2026-08-25 09:25:16');

-- ---------- 演示消息 ----------
INSERT IGNORE INTO `message` (id, user_id, type, title, content, groupon_id, is_read, created_at) VALUES (1, 1, 'APPLY_NEW', '有新的拼团申请', 'lisi 申请加入「拼晚餐，3人团」，点击查看并审批', 12, 1, '2026-08-25 06:40:49');
INSERT IGNORE INTO `message` (id, user_id, type, title, content, groupon_id, is_read, created_at) VALUES (2, 4, 'JOIN_FAIL', '拼团结果通知', '很遗憾地告知您，「拼晚餐，3人团」拼团未能成功加入，您可以选择其他拼单继续参与', NULL, 1, '2026-08-25 06:40:49');
INSERT IGNORE INTO `message` (id, user_id, type, title, content, groupon_id, is_read, created_at) VALUES (3, 1, 'APPLY_NEW', '有新的拼团申请', 'wangwu 申请加入「拼晚餐，3人团」，点击查看并审批', 12, 1, '2026-08-25 06:41:08');
INSERT IGNORE INTO `message` (id, user_id, type, title, content, groupon_id, is_read, created_at) VALUES (4, 2, 'JOIN_SUCCESS', '申请成功', '恭喜您成功加入「拼晚餐，3人团」！成团后即可查看团长与成员微信号，点击查看拼单', 12, 1, '2026-08-25 06:41:09');
INSERT IGNORE INTO `message` (id, user_id, type, title, content, groupon_id, is_read, created_at) VALUES (5, 5, 'APPLY_NEW', '有新的拼团申请', 'wangwu 申请加入「拼游戏，2人团」，点击查看并审批', 13, 1, '2026-08-25 06:44:22');
INSERT IGNORE INTO `message` (id, user_id, type, title, content, groupon_id, is_read, created_at) VALUES (6, 5, 'APPLY_NEW', '有新的拼团申请', 'wangwu 申请加入「拼雪糕，3人团」，点击查看并审批', 14, 1, '2026-08-25 06:44:58');
INSERT IGNORE INTO `message` (id, user_id, type, title, content, groupon_id, is_read, created_at) VALUES (7, 2, 'JOIN_FAIL', '拼团结果通知', '很遗憾地告知您，「拼雪糕，3人团」拼团未能成功加入，您可以选择其他拼单继续参与', NULL, 1, '2026-08-25 06:44:58');
INSERT IGNORE INTO `message` (id, user_id, type, title, content, groupon_id, is_read, created_at) VALUES (8, 5, 'APPLY_NEW', '有新的拼团申请', 'karina 申请加入「拼雪糕，3人团」，点击查看并审批', 14, 1, '2026-08-25 09:24:59');
INSERT IGNORE INTO `message` (id, user_id, type, title, content, groupon_id, is_read, created_at) VALUES (9, 2, 'JOIN_SUCCESS', '申请成功', '恭喜您成功加入「拼游戏，2人团」！成团后即可查看团长与成员微信号，点击查看拼单', 13, 0, '2026-08-25 09:25:13');
INSERT IGNORE INTO `message` (id, user_id, type, title, content, groupon_id, is_read, created_at) VALUES (10, 3, 'JOIN_SUCCESS', '申请成功', '恭喜您成功加入「拼雪糕，3人团」！成团后即可查看团长与成员微信号，点击查看拼单', 14, 1, '2026-08-25 09:25:16');

-- ---------- 管理员操作日志 ----------
INSERT IGNORE INTO `sys_log` (id, admin_id, action, target_id, detail, created_at) VALUES (1, 7, 'CANCEL_GROUPON', 12, '强制下架拼单：拼晚餐，3人团', '2026-08-26 04:31:15');
INSERT IGNORE INTO `sys_log` (id, admin_id, action, target_id, detail, created_at) VALUES (2, 7, 'BAN_USER', 8, '禁用用户：banme', '2026-08-26 04:31:15');
INSERT IGNORE INTO `sys_log` (id, admin_id, action, target_id, detail, created_at) VALUES (3, 7, 'UNBAN_USER', 8, '解除禁用：banme', '2026-08-26 04:31:15');
INSERT IGNORE INTO `sys_log` (id, admin_id, action, target_id, detail, created_at) VALUES (4, 7, 'CANCEL_GROUPON', 14, '强制下架拼单：拼雪糕，3人团', '2026-08-26 04:36:08');
INSERT IGNORE INTO `sys_log` (id, admin_id, action, target_id, detail, created_at) VALUES (5, 7, 'BAN_USER', 1, '禁用用户：zhangsan', '2026-08-26 04:43:53');
INSERT IGNORE INTO `sys_log` (id, admin_id, action, target_id, detail, created_at) VALUES (6, 7, 'UNBAN_USER', 1, '解除禁用：zhangsan', '2026-08-26 04:44:03');


-- ============================================
-- 验收清单配套账号与拼单（freshA/freshB + 拼单 15/16）
-- ============================================
INSERT IGNORE INTO `user` (id, username, password, nickname, avatar, phone, wechat, role, status, created_at) VALUES (9, 'freshA', '$2a$10$7YraBBySFowAnyn3SG7ezOlVCG7WSXFXc9pVcht9qUB1lKhs/klme', 'freshA', NULL, NULL, 'wx_freshA', 1, 1, '2026-08-26 05:30:00');
INSERT IGNORE INTO `user` (id, username, password, nickname, avatar, phone, wechat, role, status, created_at) VALUES (10, 'freshB', '$2a$10$7YraBBySFowAnyn3SG7ezOlVCG7WSXFXc9pVcht9qUB1lKhs/klme', 'freshB', NULL, NULL, 'wx_freshB', 1, 1, '2026-08-26 05:30:00');

-- 拼单 15：freshA 发布，已成团（满员）
INSERT IGNORE INTO `groupon` (id, publisher_id, title, category_tag, description, target_count, current_count, deadline, status, created_at) VALUES (15, 9, '拼水果，新鲜上市', '水果', '车厘子拼一下', 2, 2, DATE_ADD(NOW(), INTERVAL 15 DAY), 1, '2026-08-26 05:30:00');
-- 拼单 16：freshA 发布，招募中
INSERT IGNORE INTO `groupon` (id, publisher_id, title, category_tag, description, target_count, current_count, deadline, status, created_at) VALUES (16, 9, '拼奶茶，2 人团', '奶茶', NULL, 3, 1, DATE_ADD(NOW(), INTERVAL 30 DAY), 0, '2026-08-26 05:31:00');

-- 拼单 15 的成员申请记录（freshB 已加入）
INSERT IGNORE INTO `join_request` (id, groupon_id, user_id, status, created_at, updated_at) VALUES (17, 15, 10, 1, '2026-08-26 05:30:30', '2026-08-26 05:30:30');
-- 拼单 16 的成员申请记录（freshB 待确认）
INSERT IGNORE INTO `join_request` (id, groupon_id, user_id, status, created_at, updated_at) VALUES (18, 16, 10, 0, '2026-08-26 05:31:30', NULL);

-- 配套消息：freshA 收到新申请提醒（拼单 16）；freshB 收到申请通过（拼单 15）
INSERT IGNORE INTO `message` (id, user_id, type, title, content, groupon_id, is_read, created_at) VALUES (11, 9, 'APPLY_NEW', '有新申请等待审批', 'freshB 申请加入你的拼单「拼奶茶，2 人团」', 16, 0, '2026-08-26 05:31:30');
INSERT IGNORE INTO `message` (id, user_id, type, title, content, groupon_id, is_read, created_at) VALUES (12, 10, 'JOIN_SUCCESS', '申请已通过', '你已成功加入拼单「拼水果，新鲜上市」', 15, 0, '2026-08-26 05:30:30');
