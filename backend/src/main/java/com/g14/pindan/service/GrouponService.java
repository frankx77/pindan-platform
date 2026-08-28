package com.g14.pindan.service;

import com.g14.pindan.common.BizException;
import com.g14.pindan.entity.Groupon;
import com.g14.pindan.entity.JoinRequest;
import com.g14.pindan.entity.User;
import com.g14.pindan.enums.CategoryEnum;
import com.g14.pindan.mapper.GrouponMapper;
import com.g14.pindan.mapper.JoinRequestMapper;
import com.g14.pindan.mapper.UserMapper;
import com.g14.pindan.vo.MemberVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 拼单业务逻辑
 */
@Service
public class GrouponService {

    private final GrouponMapper grouponMapper;
    private final JoinRequestMapper joinRequestMapper;
    private final UserMapper userMapper;
    private final JoinRequestService joinRequestService;

    public GrouponService(GrouponMapper grouponMapper, JoinRequestMapper joinRequestMapper,
                          UserMapper userMapper, JoinRequestService joinRequestService) {
        this.grouponMapper = grouponMapper;
        this.joinRequestMapper = joinRequestMapper;
        this.userMapper = userMapper;
        this.joinRequestService = joinRequestService;
    }

    /**
     * 发布拼单
     */
    public Groupon publish(Integer publisherId, String title, String categoryTag,
                           String description, Integer targetCount, LocalDateTime deadline) {
        // 分类必须来自枚举
        if (!CategoryEnum.isValid(categoryTag)) {
            throw new BizException("分类不合法");
        }
        if (targetCount == null || targetCount < 2) {
            throw new BizException("目标人数至少为 2");
        }
        if (deadline == null || !deadline.isAfter(LocalDateTime.now())) {
            throw new BizException("截止时间必须晚于当前时间");
        }

        Groupon groupon = new Groupon();
        groupon.setPublisherId(publisherId);
        groupon.setTitle(title);
        groupon.setCategoryTag(categoryTag);
        groupon.setDescription(description);
        groupon.setTargetCount(targetCount);
        groupon.setCurrentCount(1); // 团长本人
        groupon.setDeadline(deadline);
        groupon.setStatus(0); // 招募中
        grouponMapper.insert(groupon);
        return groupon;
    }

    /**
     * 分页列表
     */
    public Map<String, Object> pageList(String categoryTag, String keyword, Integer status,
                                        int pageNum, int pageSize) {
        if (pageNum < 1) pageNum = 1;
        if (pageSize < 1 || pageSize > 50) pageSize = 10;
        int offset = (pageNum - 1) * pageSize;

        List<Groupon> list = grouponMapper.findList(categoryTag, keyword, status, offset, pageSize);
        long total = grouponMapper.countList(categoryTag, keyword, status);

        Map<String, Object> page = new HashMap<>();
        page.put("list", list);
        page.put("total", total);
        page.put("pageNum", pageNum);
        page.put("pageSize", pageSize);
        return page;
    }

    /**
     * 详情：拼单 + 团长信息 + 已加入成员 + 当前用户视角
     * 微信号保密规则：仅拼单已成团（status=1）时团长/成员微信号互相可见，其余状态隐藏
     */
    public Map<String, Object> detail(Integer id, Integer currentUserId) {
        Groupon groupon = grouponMapper.findById(id);
        if (groupon == null) {
            throw new BizException(404, "拼单不存在");
        }
        List<MemberVO> members = joinRequestMapper.findMembers(id);
        User publisher = userMapper.findById(groupon.getPublisherId());

        boolean groupComplete = groupon.getStatus() != null && groupon.getStatus() == 1;
        if (!groupComplete) {
            // 未成团：隐藏团长与成员的微信号
            publisher.setWechat(null);
            members.forEach(m -> m.setWechat(null));
        }

        Map<String, Object> data = new HashMap<>();
        data.put("groupon", groupon);
        data.put("publisher", publisher);
        data.put("memberList", members);
        // 当前用户视角
        data.put("isPublisher", groupon.getPublisherId().equals(currentUserId));
        JoinRequest myRequest = currentUserId == null ? null
                : joinRequestMapper.findByGrouponIdAndUserId(id, currentUserId);
        data.put("myJoinStatus", myRequest == null ? null : myRequest.getStatus());
        data.put("myRequestId", myRequest == null ? null : myRequest.getId());
        return data;
    }

    /**
     * 团长审批列表（含申请人信息；未成团时隐藏微信号）
     */
    public List<MemberVO> requests(Integer grouponId, Integer currentUserId) {
        Groupon groupon = grouponMapper.findById(grouponId);
        if (groupon == null) {
            throw new BizException(404, "拼单不存在");
        }
        if (!groupon.getPublisherId().equals(currentUserId)) {
            throw new BizException(403, "仅团长可查看申请列表");
        }
        List<MemberVO> list = joinRequestMapper.findRequestsWithUser(grouponId);
        boolean groupComplete = groupon.getStatus() != null && groupon.getStatus() == 1;
        if (!groupComplete) {
            list.forEach(m -> m.setWechat(null));
        }
        return list;
    }

    /**
     * 我发布的
     */
    public List<Groupon> myPublished(Integer userId) {
        return grouponMapper.findByPublisherId(userId);
    }

    /**
     * 我参加的
     */
    public List<Groupon> myJoined(Integer userId) {
        return grouponMapper.findByJoinedUserId(userId);
    }

    /**
     * 团长取消拼单（委托 JoinRequestService 保证事务一致性）
     */
    public void cancel(Integer grouponId, Integer operatorId) {
        joinRequestService.cancel(grouponId, operatorId);
    }
}
