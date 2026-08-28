package com.g14.pindan.service;

import com.g14.pindan.common.BizException;
import com.g14.pindan.entity.Groupon;
import com.g14.pindan.entity.JoinRequest;
import com.g14.pindan.mapper.GrouponMapper;
import com.g14.pindan.mapper.JoinRequestMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 手写状态机核心链路单元测试：
 * 申请（防重/防满员/防自申）→ 审批（通过自动成团/拒绝）→ 退出 → 取消（批量拒申请）
 */
class JoinRequestServiceTest {

    private JoinRequestMapper joinRequestMapper;
    private GrouponMapper grouponMapper;
    private MessageService messageService;
    private JoinRequestService service;

    private Groupon recruiting(int publisherId, int current, int target) {
        Groupon g = new Groupon();
        g.setId(100);
        g.setPublisherId(publisherId);
        g.setStatus(0);
        g.setCurrentCount(current);
        g.setTargetCount(target);
        return g;
    }

    private JoinRequest pending(int id, int grouponId, int userId) {
        JoinRequest jr = new JoinRequest();
        jr.setId(id);
        jr.setGrouponId(grouponId);
        jr.setUserId(userId);
        jr.setStatus(0);
        return jr;
    }

    @BeforeEach
    void setUp() {
        joinRequestMapper = Mockito.mock(JoinRequestMapper.class);
        grouponMapper = Mockito.mock(GrouponMapper.class);
        messageService = Mockito.mock(MessageService.class);
        service = new JoinRequestService(joinRequestMapper, grouponMapper, messageService);
    }

    // ---------- 申请 ----------

    @Test
    @DisplayName("申请成功：状态0待确认，通知团长")
    void apply_success() {
        when(grouponMapper.findById(100)).thenReturn(recruiting(1, 1, 3));
        when(joinRequestMapper.findByGrouponIdAndUserId(100, 2)).thenReturn(null);

        JoinRequest jr = service.apply(100, 2);

        assertEquals(0, jr.getStatus());
        verify(joinRequestMapper).insert(any(JoinRequest.class));
        verify(messageService).notifyApply(any(Groupon.class), any(JoinRequest.class));
    }

    @Test
    @DisplayName("重复申请被拒绝")
    void apply_duplicate() {
        when(grouponMapper.findById(100)).thenReturn(recruiting(1, 1, 3));
        when(joinRequestMapper.findByGrouponIdAndUserId(100, 2)).thenReturn(pending(9, 100, 2));

        BizException e = assertThrows(BizException.class, () -> service.apply(100, 2));
        assertEquals("您已申请过该拼单", e.getMessage());
        verify(joinRequestMapper, never()).insert(any(JoinRequest.class));
    }

    @Test
    @DisplayName("满员申请被拒绝")
    void apply_full() {
        when(grouponMapper.findById(100)).thenReturn(recruiting(1, 2, 2));
        when(joinRequestMapper.findByGrouponIdAndUserId(100, 2)).thenReturn(null);

        BizException e = assertThrows(BizException.class, () -> service.apply(100, 2));
        assertEquals("拼单人数已满", e.getMessage());
        verify(joinRequestMapper, never()).insert(any(JoinRequest.class));
    }

    @Test
    @DisplayName("非招募中状态不可申请")
    void apply_notRecruiting() {
        Groupon g = recruiting(1, 1, 3);
        g.setStatus(1); // 已成团
        when(grouponMapper.findById(100)).thenReturn(g);

        BizException e = assertThrows(BizException.class, () -> service.apply(100, 2));
        assertEquals("拼单当前不可申请（仅招募中可申请）", e.getMessage());
    }

    @Test
    @DisplayName("不能申请自己发布的拼单")
    void apply_self() {
        when(grouponMapper.findById(100)).thenReturn(recruiting(1, 1, 3));

        BizException e = assertThrows(BizException.class, () -> service.apply(100, 1));
        assertEquals("不能申请自己发布的拼单", e.getMessage());
    }

    // ---------- 审批 ----------

    @Test
    @DisplayName("审批通过：人数+1，未满员保持招募中")
    void approve_notFull() {
        when(joinRequestMapper.findById(9)).thenReturn(pending(9, 100, 2));
        when(grouponMapper.findById(100)).thenReturn(recruiting(1, 1, 3));

        service.approve(9, 1);

        verify(joinRequestMapper).updateStatus(9, 1);
        verify(grouponMapper).changeCurrentCount(100, 1);
        verify(grouponMapper, never()).updateStatus(100, 1);
        verify(messageService).notifyApprove(any(Groupon.class), any(JoinRequest.class));
    }

    @Test
    @DisplayName("审批通过且满员：自动成团 status=1")
    void approve_autoFormed() {
        when(joinRequestMapper.findById(9)).thenReturn(pending(9, 100, 2));
        when(grouponMapper.findById(100)).thenReturn(recruiting(1, 1, 2));

        service.approve(9, 1);

        verify(grouponMapper).changeCurrentCount(100, 1);
        verify(grouponMapper).updateStatus(100, 1);
    }

    @Test
    @DisplayName("非团长审批被拒绝（403）")
    void approve_notOwner() {
        when(joinRequestMapper.findById(9)).thenReturn(pending(9, 100, 2));
        when(grouponMapper.findById(100)).thenReturn(recruiting(1, 1, 3));

        BizException e = assertThrows(BizException.class, () -> service.approve(9, 3));
        assertEquals(403, e.getCode());
        assertEquals("仅团长可执行此操作", e.getMessage());
    }

    @Test
    @DisplayName("已处理的申请不可重复审批")
    void approve_alreadyHandled() {
        JoinRequest jr = pending(9, 100, 2);
        jr.setStatus(1);
        when(joinRequestMapper.findById(9)).thenReturn(jr);

        BizException e = assertThrows(BizException.class, () -> service.approve(9, 1));
        assertEquals("该申请已被处理", e.getMessage());
    }

    @Test
    @DisplayName("审批拒绝：状态2，通知申请人")
    void reject_success() {
        when(joinRequestMapper.findById(9)).thenReturn(pending(9, 100, 2));
        when(grouponMapper.findById(100)).thenReturn(recruiting(1, 3, 3));

        service.reject(9, 1);

        verify(joinRequestMapper).updateStatus(9, 2);
        verify(messageService).notifyReject(any(Groupon.class), any(JoinRequest.class));
        verify(grouponMapper, never()).changeCurrentCount(any(Integer.class), eq(0));
    }

    // ---------- 退出 ----------

    @Test
    @DisplayName("成员退出：状态3，人数-1")
    void quit_success() {
        JoinRequest jr = pending(9, 100, 2);
        jr.setStatus(1); // 已加入
        when(joinRequestMapper.findById(9)).thenReturn(jr);
        when(grouponMapper.findById(100)).thenReturn(recruiting(1, 2, 4));

        service.quit(9, 2);

        verify(joinRequestMapper).updateStatus(9, 3);
        verify(grouponMapper).changeCurrentCount(100, -1);
    }

    @Test
    @DisplayName("仅已加入成员可退出（待确认申请不可退出）")
    void quit_notMember() {
        when(joinRequestMapper.findById(9)).thenReturn(pending(9, 100, 2));

        BizException e = assertThrows(BizException.class, () -> service.quit(9, 2));
        assertEquals("仅已加入成员可退出", e.getMessage());
    }

    @Test
    @DisplayName("已成团后不可退出")
    void quit_formedGroupon() {
        JoinRequest jr = pending(9, 100, 2);
        jr.setStatus(1);
        Groupon g = recruiting(1, 3, 3);
        g.setStatus(1);
        when(joinRequestMapper.findById(9)).thenReturn(jr);
        when(grouponMapper.findById(100)).thenReturn(g);

        BizException e = assertThrows(BizException.class, () -> service.quit(9, 2));
        assertEquals("仅招募中的拼单可退出", e.getMessage());
    }

    // ---------- 取消 ----------

    @Test
    @DisplayName("团长取消：状态3，待确认申请批量拒绝")
    void cancel_success() {
        when(grouponMapper.findById(100)).thenReturn(recruiting(1, 1, 3));

        service.cancel(100, 1);

        verify(grouponMapper).updateStatus(100, 3);
        verify(joinRequestMapper).batchUpdateStatus(100, 0, 2);
    }

    @Test
    @DisplayName("已取消拼单不可重复取消")
    void cancel_alreadyCancelled() {
        Groupon g = recruiting(1, 1, 3);
        g.setStatus(3);
        when(grouponMapper.findById(100)).thenReturn(g);

        BizException e = assertThrows(BizException.class, () -> service.cancel(100, 1));
        assertEquals("拼单已取消", e.getMessage());
        verify(joinRequestMapper, never()).batchUpdateStatus(any(Integer.class), any(Integer.class), any(Integer.class));
    }
}
