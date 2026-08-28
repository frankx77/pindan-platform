package com.g14.pindan.mapper;

import com.g14.pindan.entity.SysLog;
import com.g14.pindan.vo.SysLogVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 管理员操作日志数据访问
 */
@Mapper
public interface SysLogMapper {

    @Insert("INSERT INTO `sys_log` (admin_id, action, target_id, detail) " +
            "VALUES (#{adminId}, #{action}, #{targetId}, #{detail})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SysLog sysLog);

    /** 操作日志分页查询（keyword 匹配详情/操作人用户名，action 精确筛选，按时间倒序） */
    @Select("<script>" +
            "SELECT l.id, l.admin_id AS adminId, u.username AS adminUsername, " +
            "l.action, l.target_id AS targetId, l.detail, l.created_at AS createdAt " +
            "FROM `sys_log` l LEFT JOIN `user` u ON l.admin_id = u.id WHERE 1=1 " +
            "<if test='keyword != null and keyword != \"\"'> " +
            " AND (l.detail LIKE CONCAT('%', #{keyword}, '%') OR u.username LIKE CONCAT('%', #{keyword}, '%')) </if>" +
            "<if test='action != null and action != \"\"'> AND l.action = #{action} </if>" +
            " ORDER BY l.id DESC LIMIT #{offset}, #{limit}</script>")
    List<SysLogVO> findPage(@Param("keyword") String keyword,
                            @Param("action") String action,
                            @Param("offset") int offset,
                            @Param("limit") int limit);

    /** 操作日志总数（条件与 findPage 一致） */
    @Select("<script>" +
            "SELECT COUNT(*) FROM `sys_log` l LEFT JOIN `user` u ON l.admin_id = u.id WHERE 1=1 " +
            "<if test='keyword != null and keyword != \"\"'> " +
            " AND (l.detail LIKE CONCAT('%', #{keyword}, '%') OR u.username LIKE CONCAT('%', #{keyword}, '%')) </if>" +
            "<if test='action != null and action != \"\"'> AND l.action = #{action} </if>" +
            "</script>")
    long countPage(@Param("keyword") String keyword,
                   @Param("action") String action);
}
