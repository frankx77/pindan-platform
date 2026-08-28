package com.g14.pindan.mapper;

import com.g14.pindan.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 用户数据访问
 */
@Mapper
public interface UserMapper {

    @Select("SELECT * FROM `user` WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    @Select("SELECT * FROM `user` WHERE id = #{id}")
    User findById(@Param("id") Integer id);

    @Insert("INSERT INTO `user` (username, password, nickname, avatar, phone, wechat, role, status) " +
            "VALUES (#{username}, #{password}, #{nickname}, #{avatar}, #{phone}, #{wechat}, #{role}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE `user` SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    // ---------- 管理员（阶段4） ----------

    /**
     * 用户管理列表（可用户名/昵称关键字筛选，管理员也可见）
     */
    @Select("<script>" +
            "SELECT * FROM `user` WHERE 1=1 " +
            "<if test='keyword != null and keyword != \"\"'>" +
            " AND (username LIKE CONCAT('%', #{keyword}, '%') OR nickname LIKE CONCAT('%', #{keyword}, '%'))" +
            "</if>" +
            "ORDER BY id ASC " +
            "LIMIT #{offset}, #{limit}" +
            "</script>")
    List<User> findAll(@Param("keyword") String keyword,
                       @Param("offset") int offset,
                       @Param("limit") int limit);

    @Select("<script>" +
            "SELECT COUNT(*) FROM `user` WHERE 1=1 " +
            "<if test='keyword != null and keyword != \"\"'>" +
            " AND (username LIKE CONCAT('%', #{keyword}, '%') OR nickname LIKE CONCAT('%', #{keyword}, '%'))" +
            "</if>" +
            "</script>")
    long countAll(@Param("keyword") String keyword);

    /** 看板：用户总数 */
    @Select("SELECT COUNT(*) FROM `user`")
    long countAllUsers();
}
