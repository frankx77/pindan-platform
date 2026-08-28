package com.g14.pindan.controller;

import com.g14.pindan.common.Result;
import com.g14.pindan.entity.JoinRequest;
import com.g14.pindan.service.JoinRequestService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 申请接口（状态机核心）
 */
@RestController
@RequestMapping("/api/join-request")
@Validated
public class JoinRequestController {

    private final JoinRequestService joinRequestService;

    public JoinRequestController(JoinRequestService joinRequestService) {
        this.joinRequestService = joinRequestService;
    }

    /**
     * 申请加入拼单
     */
    @PostMapping
    public Result<JoinRequest> apply(@RequestBody @Validated ApplyDTO dto, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("currentUserId");
        return Result.success(joinRequestService.apply(dto.getGrouponId(), userId));
    }

    /**
     * 团长审批通过
     */
    @PutMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Integer id, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("currentUserId");
        joinRequestService.approve(id, userId);
        return Result.success();
    }

    /**
     * 团长审批拒绝
     */
    @PutMapping("/{id}/reject")
    public Result<Void> reject(@PathVariable Integer id, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("currentUserId");
        joinRequestService.reject(id, userId);
        return Result.success();
    }

    /**
     * 成员主动退出
     */
    @PutMapping("/{id}/quit")
    public Result<Void> quit(@PathVariable Integer id, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("currentUserId");
        joinRequestService.quit(id, userId);
        return Result.success();
    }

    @Data
    public static class ApplyDTO {
        @NotNull(message = "拼单ID不能为空")
        private Integer grouponId;
    }
}
