package com.g14.pindan.service;

import com.g14.pindan.common.BizException;
import com.g14.pindan.entity.Groupon;
import com.g14.pindan.entity.SysLog;
import com.g14.pindan.entity.User;
import com.g14.pindan.mapper.GrouponMapper;
import com.g14.pindan.mapper.JoinRequestMapper;
import com.g14.pindan.mapper.SysLogMapper;
import com.g14.pindan.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 管理员治理单元测试：强制下架留痕、禁用/解禁边界（不能动自己与管理员）
 */
class AdminServiceTest {

    private GrouponMapper grouponMapper;
    private JoinRequestMapper joinRequestMapper;
    private UserMapper userMapper;
    private SysLogMapper sysLogMapper;
    private AdminService service;

    @BeforeEach
    void setUp() {
        grouponMapper = Mockito.mock(GrouponMapper.class);
        joinRequestMapper = Mockito.mock(JoinRequestMapper.class);
        userMapper = Mockito.mock(UserMapper.class);
        sysLogMapper = Mockito.mock(SysLogMapper.class);
        service = new AdminService(grouponMapper, joinRequestMapper, userMapper, sysLogMapper);
    }

    private User user(int id, String username, Integer role, Integer status) {
        User u = new User();
        u.setId(id);
        u.setUsername(username);
        u.setRole(role);
        u.setStatus(status);
        return u;
    }

    private Groupon groupon(int id, int status, String title) {
        Groupon g = new Groupon();
        g.setId(id);
        g.setStatus(status);
        g.setTitle(title);
        return g;
    }

    @Test
    @DisplayName("强制下架：状态3 + 批量拒待确认 + 写操作日志")
    void cancel_writesLog() {
        when(grouponMapper.findById(12)).thenReturn(groupon(12, 0, "拼超市"));

        service.cancel(12, 7);

        verify(grouponMapper).updateStatus(12, 3);
        verify(joinRequestMapper).batchUpdateStatus(12, 0, 2);
        verify(sysLogMapper).insert(any(SysLog.class));
    }

    @Test
    @DisplayName("下架已取消拼单：报错且不重复处理")
    void cancel_alreadyCancelled() {
        when(grouponMapper.findById(12)).thenReturn(groupon(12, 3, "拼超市"));

        BizException e = assertThrows(BizException.class, () -> service.cancel(12, 7));
        assertEquals("拼单已取消，无需重复下架", e.getMessage());
        verify(sysLogMapper, never()).insert(any(SysLog.class));
    }

    @Test
    @DisplayName("不能禁用自己")
    void ban_self() {
        when(userMapper.findById(7)).thenReturn(user(7, "admin", 2, 1));

        BizException e = assertThrows(BizException.class, () -> service.banUser(7, 7));
        assertEquals("不能操作自己的账号", e.getMessage());
        verify(userMapper, never()).updateStatus(any(Integer.class), any(Integer.class));
    }

    @Test
    @DisplayName("不能禁用管理员账号")
    void ban_admin() {
        when(userMapper.findById(8)).thenReturn(user(8, "boss", 2, 1));

        BizException e = assertThrows(BizException.class, () -> service.banUser(8, 7));
        assertEquals("不能操作管理员账号", e.getMessage());
    }

    @Test
    @DisplayName("禁用成功：状态0 + 写日志")
    void ban_ok() {
        when(userMapper.findById(5)).thenReturn(user(5, "zhangsan", 1, 1));

        service.banUser(5, 7);

        verify(userMapper).updateStatus(5, 0);
        verify(sysLogMapper).insert(any(SysLog.class));
    }

    @Test
    @DisplayName("解除禁用：状态1 + 写日志")
    void unban_ok() {
        when(userMapper.findById(5)).thenReturn(user(5, "zhangsan", 1, 0));

        service.unbanUser(5, 7);

        verify(userMapper).updateStatus(5, 1);
        verify(sysLogMapper).insert(any(SysLog.class));
    }
}
