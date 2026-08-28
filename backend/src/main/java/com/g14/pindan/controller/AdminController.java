package com.g14.pindan.controller;

import com.g14.pindan.common.Result;
import com.g14.pindan.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 管理员接口（阶段4）
 * 访问权限由 AdminInterceptor 统一校验（仅 role=2 管理员）
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * 4.1 管理员全量拼单列表（keyword/status 可选筛选，分页）
     */
    @GetMapping("/groupon/list")
    public Result<Map<String, Object>> grouponList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(adminService.pageList(keyword, status, pageNum, pageSize));
    }

    /**
     * 4.2 管理员强制下架拼单（写 sys_log）
     */
    @PutMapping("/groupon/{id}/cancel")
    public Result<Void> cancelGroupon(@PathVariable Integer id, HttpServletRequest request) {
        Integer adminId = (Integer) request.getAttribute("currentUserId");
        adminService.cancel(id, adminId);
        return Result.success();
    }

    /**
     * 4.3 管理员禁用用户
     */
    @PutMapping("/user/{id}/ban")
    public Result<Void> banUser(@PathVariable Integer id, HttpServletRequest request) {
        Integer adminId = (Integer) request.getAttribute("currentUserId");
        adminService.banUser(id, adminId);
        return Result.success();
    }

    /**
     * 解除禁用
     */
    @PutMapping("/user/{id}/unban")
    public Result<Void> unbanUser(@PathVariable Integer id, HttpServletRequest request) {
        Integer adminId = (Integer) request.getAttribute("currentUserId");
        adminService.unbanUser(id, adminId);
        return Result.success();
    }

    /**
     * 用户管理列表（keyword 可选筛选，分页）
     */
    @GetMapping("/user/list")
    public Result<Map<String, Object>> userList(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(adminService.userPageList(keyword, pageNum, pageSize));
    }

    /**
     * 操作日志分页查询（keyword 匹配详情/操作人，action 精确筛选）
     */
    @GetMapping("/log/list")
    public Result<Map<String, Object>> logList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String action,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(adminService.logPageList(keyword, action, pageNum, pageSize));
    }

    /**
     * 4.4 看板聚合数据
     */
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        return Result.success(adminService.dashboard());
    }
}
