package com.g14.pindan.service;

import com.g14.pindan.common.BizException;
import com.g14.pindan.entity.Groupon;
import com.g14.pindan.entity.SysLog;
import com.g14.pindan.entity.User;
import com.g14.pindan.mapper.GrouponMapper;
import com.g14.pindan.mapper.JoinRequestMapper;
import com.g14.pindan.mapper.SysLogMapper;
import com.g14.pindan.mapper.UserMapper;
import com.g14.pindan.vo.AdminGrouponVO;
import com.g14.pindan.vo.SysLogVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员业务逻辑（阶段4）：全量拼单、强制下架、禁用用户、看板聚合
 * 接口权限在 AdminInterceptor 统一校验（role=2）
 */
@Service
public class AdminService {

    private final GrouponMapper grouponMapper;
    private final JoinRequestMapper joinRequestMapper;
    private final UserMapper userMapper;
    private final SysLogMapper sysLogMapper;

    public AdminService(GrouponMapper grouponMapper, JoinRequestMapper joinRequestMapper,
                        UserMapper userMapper, SysLogMapper sysLogMapper) {
        this.grouponMapper = grouponMapper;
        this.joinRequestMapper = joinRequestMapper;
        this.userMapper = userMapper;
        this.sysLogMapper = sysLogMapper;
    }

    /**
     * 4.1 管理员全量拼单列表（无用户过滤）
     */
    public Map<String, Object> pageList(String keyword, Integer status, int pageNum, int pageSize) {
        if (pageNum < 1) pageNum = 1;
        if (pageSize < 1 || pageSize > 50) pageSize = 10;
        int offset = (pageNum - 1) * pageSize;

        List<AdminGrouponVO> list = grouponMapper.findAllAdmin(keyword, status, offset, pageSize);
        long total = grouponMapper.countAllAdmin(keyword, status);

        Map<String, Object> page = new HashMap<>();
        page.put("list", list);
        page.put("total", total);
        page.put("pageNum", pageNum);
        page.put("pageSize", pageSize);
        return page;
    }

    /**
     * 4.2 管理员强制下架：status=3 + 批量拒绝待确认申请 + 写 sys_log
     */
    @Transactional
    public void cancel(Integer grouponId, Integer adminId) {
        Groupon groupon = grouponMapper.findById(grouponId);
        if (groupon == null) {
            throw new BizException(404, "拼单不存在");
        }
        if (groupon.getStatus() == 3) {
            throw new BizException("拼单已取消，无需重复下架");
        }
        grouponMapper.updateStatus(grouponId, 3);
        joinRequestMapper.batchUpdateStatus(grouponId, 0, 2);

        SysLog log = new SysLog();
        log.setAdminId(adminId);
        log.setAction("CANCEL_GROUPON");
        log.setTargetId(grouponId);
        log.setDetail("强制下架拼单：" + groupon.getTitle());
        sysLogMapper.insert(log);
    }

    /**
     * 4.3 管理员禁用用户（不能禁用自己，不能禁用管理员）
     */
    @Transactional
    public void banUser(Integer userId, Integer adminId) {
        changeUserStatus(userId, adminId, 0, "BAN_USER", "禁用用户");
    }

    /**
     * 解除禁用（与禁用成对，便于演示与误操作恢复）
     */
    @Transactional
    public void unbanUser(Integer userId, Integer adminId) {
        changeUserStatus(userId, adminId, 1, "UNBAN_USER", "解除禁用");
    }

    private void changeUserStatus(Integer userId, Integer adminId, int status, String action, String verb) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BizException(404, "用户不存在");
        }
        if (user.getId().equals(adminId)) {
            throw new BizException("不能操作自己的账号");
        }
        if (user.getRole() != null && user.getRole() == 2) {
            throw new BizException("不能操作管理员账号");
        }
        userMapper.updateStatus(userId, status);

        SysLog log = new SysLog();
        log.setAdminId(adminId);
        log.setAction(action);
        log.setTargetId(userId);
        log.setDetail(verb + "：" + user.getUsername());
        sysLogMapper.insert(log);
    }

    /**
     * 用户管理列表
     */
    public Map<String, Object> userPageList(String keyword, int pageNum, int pageSize) {
        if (pageNum < 1) pageNum = 1;
        if (pageSize < 1 || pageSize > 50) pageSize = 10;
        int offset = (pageNum - 1) * pageSize;

        List<User> list = userMapper.findAll(keyword, offset, pageSize);
        long total = userMapper.countAll(keyword);

        Map<String, Object> page = new HashMap<>();
        page.put("list", list);
        page.put("total", total);
        page.put("pageNum", pageNum);
        page.put("pageSize", pageSize);
        return page;
    }

    /**
     * 操作日志分页查询（keyword/action 可选筛选）
     */
    public Map<String, Object> logPageList(String keyword, String action, int pageNum, int pageSize) {
        if (pageNum < 1) pageNum = 1;
        if (pageSize < 1 || pageSize > 50) pageSize = 10;
        int offset = (pageNum - 1) * pageSize;

        List<SysLogVO> list = sysLogMapper.findPage(keyword, action, offset, pageSize);
        long total = sysLogMapper.countPage(keyword, action);

        Map<String, Object> page = new HashMap<>();
        page.put("list", list);
        page.put("total", total);
        page.put("pageNum", pageNum);
        page.put("pageSize", pageSize);
        return page;
    }

    /**
     * 4.4 看板聚合：拼单总数、状态分布、TOP5 分类、用户总数
     */
    public Map<String, Object> dashboard() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalGroupons", grouponMapper.countAll());
        data.put("totalUsers", userMapper.countAllUsers());
        data.put("statusStats", grouponMapper.countByStatus());
        data.put("topCategories", grouponMapper.countByCategory());
        return data;
    }
}
