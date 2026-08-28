package com.g14.pindan.service;

import com.g14.pindan.common.BizException;
import com.g14.pindan.entity.Groupon;
import com.g14.pindan.entity.JoinRequest;
import com.g14.pindan.mapper.GrouponMapper;
import com.g14.pindan.mapper.JoinRequestMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 申请与状态机业务逻辑（核心得分点）
 * 状态流转必须在此层手写 if-else，禁止状态机框架
 */
@Service
public class JoinRequestService {

    private final JoinRequestMapper joinRequestMapper;
    private final GrouponMapper grouponMapper;
    private final MessageService messageService;

    public JoinRequestService(JoinRequestMapper joinRequestMapper, GrouponMapper grouponMapper,
                              MessageService messageService) {
        this.joinRequestMapper = joinRequestMapper;
        this.grouponMapper = grouponMapper;
        this.messageService = messageService;
    }

    /**
     * 申请加入（3.2）
     * 校验：拼单存在且招募中、非团长本人、未申请过、未满员
     */
    public JoinRequest apply(Integer grouponId, Integer userId) {
        Groupon groupon = grouponMapper.findById(grouponId);
        if (groupon == null) {
            throw new BizException(404, "拼单不存在");
        }
        if (groupon.getStatus() != 0) {
            throw new BizException("拼单当前不可申请（仅招募中可申请）");
        }
        if (groupon.getPublisherId().equals(userId)) {
            throw new BizException("不能申请自己发布的拼单");
        }
        if (joinRequestMapper.findByGrouponIdAndUserId(grouponId, userId) != null) {
            throw new BizException("您已申请过该拼单");
        }
        if (groupon.getCurrentCount() >= groupon.getTargetCount()) {
            throw new BizException("拼单人数已满");
        }

        JoinRequest jr = new JoinRequest();
        jr.setGrouponId(grouponId);
        jr.setUserId(userId);
        jr.setStatus(0); // 待确认
        joinRequestMapper.insert(jr);

        // 给团长发新申请提醒（可点击跳转审批）
        messageService.notifyApply(groupon, jr);
        return jr;
    }

    /**
     * 团长审批通过（3.3）
     * jr.status=1；current_count+1；若达到目标则自动成团 status=1
     */
    @Transactional
    public void approve(Integer requestId, Integer operatorId) {
        JoinRequest jr = requirePendingRequest(requestId);
        Groupon groupon = requireOwner(operatorId, jr.getGrouponId());
        if (groupon.getStatus() != 0) {
            throw new BizException("拼单当前不可审批");
        }
        if (groupon.getCurrentCount() >= groupon.getTargetCount()) {
            throw new BizException("拼单人数已满");
        }

        // 通过申请
        joinRequestMapper.updateStatus(requestId, 1);
        // 人数 +1
        grouponMapper.changeCurrentCount(groupon.getId(), 1);
        // 立即判断是否成团
        if (groupon.getCurrentCount() + 1 >= groupon.getTargetCount()) {
            grouponMapper.updateStatus(groupon.getId(), 1);
        }
        // 给成员发申请成功通知（可点击查看拼单）
        messageService.notifyApprove(groupon, jr);
    }

    /**
     * 团长审批拒绝（3.4）
     */
    @Transactional
    public void reject(Integer requestId, Integer operatorId) {
        JoinRequest jr = requirePendingRequest(requestId);
        requireOwner(operatorId, jr.getGrouponId());
        joinRequestMapper.updateStatus(requestId, 2);
        // 给成员发申请失败通知（纯提示，不可点击）
        Groupon groupon = grouponMapper.findById(jr.getGrouponId());
        messageService.notifyReject(groupon, jr);
    }

    /**
     * 成员主动退出（3.6）
     * 仅当拼单招募中可退出；jr.status=3；current_count-1
     */
    @Transactional
    public void quit(Integer requestId, Integer userId) {
        JoinRequest jr = joinRequestMapper.findById(requestId);
        if (jr == null || !jr.getUserId().equals(userId)) {
            throw new BizException(404, "申请记录不存在");
        }
        if (jr.getStatus() != 1) {
            throw new BizException("仅已加入成员可退出");
        }
        Groupon groupon = grouponMapper.findById(jr.getGrouponId());
        if (groupon == null) {
            throw new BizException(404, "拼单不存在");
        }
        if (groupon.getStatus() != 0) {
            throw new BizException("仅招募中的拼单可退出");
        }

        joinRequestMapper.updateStatus(requestId, 3);
        grouponMapper.changeCurrentCount(groupon.getId(), -1);
    }

    /**
     * 团长取消拼单（3.5）
     * groupon.status=3；批量拒绝所有待确认申请
     */
    @Transactional
    public void cancel(Integer grouponId, Integer operatorId) {
        Groupon groupon = requireOwner(operatorId, grouponId);
        if (groupon.getStatus() == 3) {
            throw new BizException("拼单已取消");
        }
        grouponMapper.updateStatus(grouponId, 3);
        // 所有待确认申请 → 已拒绝
        joinRequestMapper.batchUpdateStatus(grouponId, 0, 2);
    }

    // ---------- 私有工具 ----------

    private JoinRequest requirePendingRequest(Integer requestId) {
        JoinRequest jr = joinRequestMapper.findById(requestId);
        if (jr == null) {
            throw new BizException(404, "申请记录不存在");
        }
        if (jr.getStatus() != 0) {
            throw new BizException("该申请已被处理");
        }
        return jr;
    }

    private Groupon requireOwner(Integer operatorId, Integer grouponId) {
        Groupon groupon = grouponMapper.findById(grouponId);
        if (groupon == null) {
            throw new BizException(404, "拼单不存在");
        }
        if (!groupon.getPublisherId().equals(operatorId)) {
            throw new BizException(403, "仅团长可执行此操作");
        }
        return groupon;
    }
}
