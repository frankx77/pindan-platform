package com.g14.pindan.controller;

import com.g14.pindan.common.Result;
import com.g14.pindan.entity.Message;
import com.g14.pindan.service.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息接口
 */
@RestController
@RequestMapping("/api/message")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * 我的消息列表（未读在前，时间倒序）
     */
    @GetMapping("/list")
    public Result<List<Message>> list(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("currentUserId");
        return Result.success(messageService.list(userId));
    }

    /**
     * 未读数（Tabbar 角标）
     */
    @GetMapping("/unread-count")
    public Result<Map<String, Object>> unreadCount(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("currentUserId");
        Map<String, Object> data = new HashMap<>();
        data.put("count", messageService.unreadCount(userId));
        return Result.success(data);
    }

    /**
     * 单条标记已读
     */
    @PutMapping("/{id}/read")
    public Result<Void> markRead(@PathVariable Integer id, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("currentUserId");
        messageService.markRead(id, userId);
        return Result.success();
    }

    /**
     * 全部标记已读
     */
    @PutMapping("/read-all")
    public Result<Void> markAllRead(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("currentUserId");
        messageService.markAllRead(userId);
        return Result.success();
    }
}
