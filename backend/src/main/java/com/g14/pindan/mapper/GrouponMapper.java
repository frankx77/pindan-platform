package com.g14.pindan.mapper;

import com.g14.pindan.entity.Groupon;
import com.g14.pindan.vo.AdminGrouponVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * 拼单数据访问
 */
@Mapper
public interface GrouponMapper {

    @Insert("INSERT INTO `groupon` (publisher_id, title, category_tag, description, target_count, current_count, deadline, status) " +
            "VALUES (#{publisherId}, #{title}, #{categoryTag}, #{description}, #{targetCount}, #{currentCount}, #{deadline}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Groupon groupon);

    @Select("SELECT * FROM `groupon` WHERE id = #{id}")
    Groupon findById(@Param("id") Integer id);

    /**
     * 分页 + 条件筛选列表
     * categoryTag / keyword / status 均可为空
     */
    @Select("<script>" +
            "SELECT * FROM `groupon` WHERE 1=1 " +
            "<if test='categoryTag != null and categoryTag != \"\"'> AND category_tag = #{categoryTag} </if>" +
            "<if test='keyword != null and keyword != \"\"'> AND title LIKE CONCAT('%', #{keyword}, '%') </if>" +
            "<if test='status != null'> AND status = #{status} </if>" +
            "ORDER BY created_at DESC " +
            "LIMIT #{offset}, #{limit}" +
            "</script>")
    List<Groupon> findList(@Param("categoryTag") String categoryTag,
                           @Param("keyword") String keyword,
                           @Param("status") Integer status,
                           @Param("offset") int offset,
                           @Param("limit") int limit);

    @Select("<script>" +
            "SELECT COUNT(*) FROM `groupon` WHERE 1=1 " +
            "<if test='categoryTag != null and categoryTag != \"\"'> AND category_tag = #{categoryTag} </if>" +
            "<if test='keyword != null and keyword != \"\"'> AND title LIKE CONCAT('%', #{keyword}, '%') </if>" +
            "<if test='status != null'> AND status = #{status} </if>" +
            "</script>")
    long countList(@Param("categoryTag") String categoryTag,
                   @Param("keyword") String keyword,
                   @Param("status") Integer status);

    @Select("SELECT * FROM `groupon` WHERE publisher_id = #{publisherId} ORDER BY created_at DESC")
    List<Groupon> findByPublisherId(@Param("publisherId") Integer publisherId);

    /**
     * 我参加的拼单（申请已加入 status=1）
     */
    @Select("SELECT g.* FROM `groupon` g " +
            "INNER JOIN `join_request` jr ON jr.groupon_id = g.id " +
            "WHERE jr.user_id = #{userId} AND jr.status = 1 " +
            "ORDER BY g.created_at DESC")
    List<Groupon> findByJoinedUserId(@Param("userId") Integer userId);

    @Update("UPDATE `groupon` SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    /**
     * 人数变更（审批通过 +1 / 退出 -1）
     */
    @Update("UPDATE `groupon` SET current_count = current_count + #{delta} WHERE id = #{id}")
    int changeCurrentCount(@Param("id") Integer id, @Param("delta") int delta);

    /**
     * 截止超时批量结束：招募中且已过截止时间 → 已结束
     */
    @Update("UPDATE `groupon` SET status = 2 WHERE status = 0 AND deadline IS NOT NULL AND deadline < NOW()")
    int closeExpired();

    // ---------- 管理员（阶段4） ----------

    /**
     * 管理员全量拼单列表（可关键字/状态筛选），带团长昵称
     */
    @Select("<script>" +
            "SELECT g.*, u.nickname AS publisher_nickname, u.username AS publisher_username " +
            "FROM `groupon` g " +
            "LEFT JOIN `user` u ON u.id = g.publisher_id " +
            "WHERE 1=1 " +
            "<if test='keyword != null and keyword != \"\"'> AND g.title LIKE CONCAT('%', #{keyword}, '%') </if>" +
            "<if test='status != null'> AND g.status = #{status} </if>" +
            "ORDER BY g.created_at DESC " +
            "LIMIT #{offset}, #{limit}" +
            "</script>")
    List<AdminGrouponVO> findAllAdmin(@Param("keyword") String keyword,
                                      @Param("status") Integer status,
                                      @Param("offset") int offset,
                                      @Param("limit") int limit);

    @Select("<script>" +
            "SELECT COUNT(*) FROM `groupon` g WHERE 1=1 " +
            "<if test='keyword != null and keyword != \"\"'> AND g.title LIKE CONCAT('%', #{keyword}, '%') </if>" +
            "<if test='status != null'> AND g.status = #{status} </if>" +
            "</script>")
    long countAllAdmin(@Param("keyword") String keyword,
                       @Param("status") Integer status);

    /** 看板：拼单总数 */
    @Select("SELECT COUNT(*) FROM `groupon`")
    long countAll();

    /** 看板：各状态数量 [{status, count}] */
    @Select("SELECT status, COUNT(*) AS count FROM `groupon` GROUP BY status ORDER BY status")
    List<Map<String, Object>> countByStatus();

    /** 看板：TOP5 分类 [{categoryTag, count}] */
    @Select("SELECT category_tag AS categoryTag, COUNT(*) AS count FROM `groupon` " +
            "GROUP BY category_tag ORDER BY count DESC LIMIT 5")
    List<Map<String, Object>> countByCategory();
}
