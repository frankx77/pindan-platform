package com.g14.pindan.controller;

import com.g14.pindan.common.Result;
import com.g14.pindan.entity.Groupon;
import com.g14.pindan.service.ExcelExportService;
import com.g14.pindan.service.GrouponService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 拼单接口
 */
@RestController
@RequestMapping("/api/groupon")
@Validated
public class GrouponController {

    private final GrouponService grouponService;
    private final ExcelExportService excelExportService;

    public GrouponController(GrouponService grouponService, ExcelExportService excelExportService) {
        this.grouponService = grouponService;
        this.excelExportService = excelExportService;
    }

    /**
     * 发布拼单
     */
    @PostMapping
    public Result<Groupon> publish(@RequestBody @Validated PublishDTO dto, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("currentUserId");
        Groupon groupon = grouponService.publish(
                userId, dto.getTitle(), dto.getCategoryTag(),
                dto.getDescription(), dto.getTargetCount(), dto.getDeadline());
        return Result.success(groupon);
    }

    /**
     * 分页列表（categoryTag/keyword/status 均可选）
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(required = false) String categoryTag,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(grouponService.pageList(categoryTag, keyword, status, pageNum, pageSize));
    }

    /**
     * 详情
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Integer id, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("currentUserId");
        return Result.success(grouponService.detail(id, userId));
    }

    /**
     * 团长审批列表（含申请人信息）
     */
    @GetMapping("/{id}/requests")
    public Result<List<com.g14.pindan.vo.MemberVO>> requests(@PathVariable Integer id, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("currentUserId");
        return Result.success(grouponService.requests(id, userId));
    }

    /**
     * 团长取消拼单
     */
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Integer id, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("currentUserId");
        grouponService.cancel(id, userId);
        return Result.success();
    }

    /**
     * 我发布的
     */
    @GetMapping("/my/published")
    public Result<List<Groupon>> myPublished(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("currentUserId");
        return Result.success(grouponService.myPublished(userId));
    }

    /**
     * 我参加的
     */
    @GetMapping("/my/joined")
    public Result<List<Groupon>> myJoined(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("currentUserId");
        return Result.success(grouponService.myJoined(userId));
    }

    /**
     * 5.1 Excel 导出：我发布的拼单
     */
    @GetMapping("/my/published/export")
    public void exportPublished(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer userId = (Integer) request.getAttribute("currentUserId");
        byte[] data = excelExportService.exportGroupons(grouponService.myPublished(userId), "我发布的拼单");
        writeExcel(response, data, "我发布的拼单.xlsx");
    }

    /**
     * 5.2 Excel 导出：我参加的拼单
     */
    @GetMapping("/my/joined/export")
    public void exportJoined(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer userId = (Integer) request.getAttribute("currentUserId");
        byte[] data = excelExportService.exportGroupons(grouponService.myJoined(userId), "我参加的拼单");
        writeExcel(response, data, "我参加的拼单.xlsx");
    }

    private void writeExcel(HttpServletResponse response, byte[] data, String filename) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        // 中文文件名：RFC 5987 编码（filename*=UTF-8''），旧式 filename 用 ASCII 兜底
        response.setHeader("Content-Disposition",
                "attachment; filename=export.xlsx; filename*=UTF-8''"
                        + URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20"));
        response.getOutputStream().write(data);
        response.getOutputStream().flush();
    }

    @Data
    public static class PublishDTO {
        @NotBlank(message = "标题不能为空")
        @Size(max = 100, message = "标题不能超过 100 字")
        private String title;

        @NotBlank(message = "请选择分类")
        private String categoryTag;

        private String description;

        @NotNull(message = "请填写目标人数")
        @Min(value = 2, message = "目标人数至少为 2")
        private Integer targetCount;

        @NotNull(message = "请选择截止时间")
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime deadline;
    }
}
