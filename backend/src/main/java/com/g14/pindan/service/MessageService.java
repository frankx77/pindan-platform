package com.g14.pindan.service;

import com.g14.pindan.entity.Groupon;
import com.g14.pindan.entity.JoinRequest;
import com.g14.pindan.entity.Message;
import com.g14.pindan.entity.User;
import com.g14.pindan.mapper.GrouponMapper;
import com.g14.pindan.mapper.MessageMapper;
import com.g14.pindan.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 消息业务逻辑
 */
@Service
public class MessageService {

    private final MessageMapper messageMapper;
    private final GrouponMapper grouponMapper;
    private final UserMapper userMapper;

    public MessageService(MessageMapper messageMapper, GrouponMapper grouponMapper, UserMapper userMapper) {
        this.messageMapper = messageMapper;
        this.grouponMapper = grouponMapper;
        this.userMapper = userMapper;
    }

    /**
     * 团长新申请提醒（可点击跳转拼单详情审批）
     */
    public void notifyApply(Groupon groupon, JoinRequest jr) {
        User applicant = userMapper.findById(jr.getUserId());
        insert(groupon.getPublisherId(), "APPLY_NEW",
                "有新的拼团申请",
                (applicant != null ? (applicant.getNickname() != null ? applicant.getNickname() : applicant.getUsername()) : "有人")
                        + " 申请加入「" + groupon.getTitle() + "」，点击查看并审批",
                groupon.getId());
    }

    /**
     * 成员申请成功（可点击跳转拼单详情）
     */
    public void notifyApprove(Groupon groupon, JoinRequest jr) {
        insert(jr.getUserId(), "JOIN_SUCCESS",
                "申请成功",
                "恭喜您成功加入「" + groupon.getTitle() + "」！成团后即可查看团长与成员微信号，点击查看拼单",
                groupon.getId());
    }

    /**
     * 成员申请失败（纯通知，不可点击跳转）
     * 文案注意：不出现"拒绝/被拒绝"字样
     */
    public void notifyReject(Groupon groupon, JoinRequest jr) {
        insert(jr.getUserId(), "JOIN_FAIL",
                "拼团结果通知",
                "很遗憾地告知您，「" + groupon.getTitle() + "」拼团未能成功加入，您可以选择其他拼单继续参与",
                null);
    }

    private void insert(Integer userId, String type, String title, String content, Integer grouponId) {
        Message msg = new Message();
        msg.setUserId(userId);
        msg.setType(type);
        msg.setTitle(title);
        msg.setContent(content);
        msg.setGrouponId(grouponId);
        msg.setIsRead(0);
        messageMapper.insert(msg);
    }

    public List<Message> list(Integer userId) {
        return messageMapper.findByUserId(userId);
    }

    public long unreadCount(Integer userId) {
        return messageMapper.countUnread(userId);
    }

    public void markRead(Integer id, Integer userId) {
        messageMapper.markRead(id, userId);
    }

    public void markAllRead(Integer userId) {
        messageMapper.markAllRead(userId);
    }
}
