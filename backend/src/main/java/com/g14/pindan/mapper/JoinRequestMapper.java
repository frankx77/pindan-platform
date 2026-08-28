package com.g14.pindan.mapper;

import com.g14.pindan.entity.JoinRequest;
import com.g14.pindan.vo.MemberVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 申请记录数据访问
 */
@Mapper
public interface JoinRequestMapper {

    @Insert("INSERT INTO `join_request` (groupon_id, user_id, status) " +
            "VALUES (#{grouponId}, #{userId}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(JoinRequest joinRequest);

    @Select("SELECT * FROM `join_request` WHERE groupon_id = #{grouponId} AND user_id = #{userId}")
    JoinRequest findByGrouponIdAndUserId(@Param("grouponId") Integer grouponId,
                                         @Param("userId") Integer userId);

    @Select("SELECT * FROM `join_request` WHERE groupon_id = #{grouponId} ORDER BY created_at ASC")
    List<JoinRequest> findByGrouponId(@Param("grouponId") Integer grouponId);

    @Select("SELECT * FROM `join_request` WHERE id = #{id}")
    JoinRequest findById(@Param("id") Integer id);

    /**
     * 已加入成员列表（status=1），含用户信息，供详情页展示
     */
    @Select("SELECT jr.id AS id, jr.groupon_id AS grouponId, jr.user_id AS userId, jr.status, " +
            "jr.created_at AS createdAt, u.username, u.nickname, u.avatar, u.wechat " +
            "FROM `join_request` jr " +
            "INNER JOIN `user` u ON u.id = jr.user_id " +
            "WHERE jr.groupon_id = #{grouponId} AND jr.status = 1 " +
            "ORDER BY jr.updated_at ASC")
    List<MemberVO> findMembers(@Param("grouponId") Integer grouponId);

    /**
     * 团长查看申请列表（全部状态），含用户信息
     */
    @Select("SELECT jr.id AS id, jr.groupon_id AS grouponId, jr.user_id AS userId, jr.status, " +
            "jr.created_at AS createdAt, u.username, u.nickname, u.avatar, u.wechat " +
            "FROM `join_request` jr " +
            "INNER JOIN `user` u ON u.id = jr.user_id " +
            "WHERE jr.groupon_id = #{grouponId} " +
            "ORDER BY jr.status ASC, jr.created_at ASC")
    List<MemberVO> findRequestsWithUser(@Param("grouponId") Integer grouponId);

    @Update("UPDATE `join_request` SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    /**
     * 批量更新某拼单下所有指定状态的申请（如团长取消时拒绝全部待确认）
     */
    @Update("UPDATE `join_request` SET status = #{newStatus}, updated_at = NOW() " +
            "WHERE groupon_id = #{grouponId} AND status = #{oldStatus}")
    int batchUpdateStatus(@Param("grouponId") Integer grouponId,
                          @Param("oldStatus") Integer oldStatus,
                          @Param("newStatus") Integer newStatus);
}
