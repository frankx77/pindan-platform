package com.g14.pindan.mapper;

import com.g14.pindan.entity.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 消息数据访问
 */
@Mapper
public interface MessageMapper {

    @Insert("INSERT INTO `message` (user_id, type, title, content, groupon_id, is_read) " +
            "VALUES (#{userId}, #{type}, #{title}, #{content}, #{grouponId}, #{isRead})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Message message);

    /**
     * 当前用户消息列表：未读在前，时间倒序，最多 50 条
     */
    @Select("SELECT * FROM `message` WHERE user_id = #{userId} " +
            "ORDER BY is_read ASC, created_at DESC LIMIT 50")
    List<Message> findByUserId(@Param("userId") Integer userId);

    /**
     * 未读数
     */
    @Select("SELECT COUNT(*) FROM `message` WHERE user_id = #{userId} AND is_read = 0")
    long countUnread(@Param("userId") Integer userId);

    /**
     * 单条标记已读（校验归属）
     */
    @Update("UPDATE `message` SET is_read = 1 WHERE id = #{id} AND user_id = #{userId}")
    int markRead(@Param("id") Integer id, @Param("userId") Integer userId);

    /**
     * 全部标记已读
     */
    @Update("UPDATE `message` SET is_read = 1 WHERE user_id = #{userId} AND is_read = 0")
    int markAllRead(@Param("userId") Integer userId);
}
