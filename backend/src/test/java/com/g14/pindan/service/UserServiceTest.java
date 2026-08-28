package com.g14.pindan.service;

import com.g14.pindan.common.BizException;
import com.g14.pindan.entity.User;
import com.g14.pindan.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * 登录与注册单元测试：BCrypt 校验、禁用账号拒绝、用户名唯一
 */
class UserServiceTest {

    private UserMapper userMapper;
    private UserService service;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        userMapper = Mockito.mock(UserMapper.class);
        service = new UserService(userMapper);
    }

    private User user(String username, String rawPassword, int status) {
        User u = new User();
        u.setId(1);
        u.setUsername(username);
        u.setPassword(encoder.encode(rawPassword));
        u.setRole(1);
        u.setStatus(status);
        return u;
    }

    @Test
    @DisplayName("登录成功：BCrypt 密码匹配返回用户")
    void login_ok() {
        when(userMapper.findByUsername("zhangsan")).thenReturn(user("zhangsan", "123456", 1));

        User u = service.login("zhangsan", "123456");

        assertEquals("zhangsan", u.getUsername());
        assertEquals(1, u.getStatus());
    }

    @Test
    @DisplayName("密码错误：报账号或密码错误（不泄露账号是否存在）")
    void login_wrongPassword() {
        when(userMapper.findByUsername("zhangsan")).thenReturn(user("zhangsan", "123456", 1));

        BizException e = assertThrows(BizException.class, () -> service.login("zhangsan", "wrong"));
        assertEquals("用户名或密码错误", e.getMessage());
    }

    @Test
    @DisplayName("账号不存在：报同样的通用错误")
    void login_notFound() {
        when(userMapper.findByUsername("nobody")).thenReturn(null);

        BizException e = assertThrows(BizException.class, () -> service.login("nobody", "123456"));
        assertEquals("用户名或密码错误", e.getMessage());
    }

    @Test
    @DisplayName("禁用账号登录被拒绝")
    void login_disabled() {
        when(userMapper.findByUsername("banned")).thenReturn(user("banned", "123456", 0));

        BizException e = assertThrows(BizException.class, () -> service.login("banned", "123456"));
        assertEquals("账号已被禁用，请联系管理员", e.getMessage());
    }

    @Test
    @DisplayName("注册：用户名重复被拒绝")
    void register_duplicate() {
        when(userMapper.findByUsername("zhangsan")).thenReturn(user("zhangsan", "123456", 1));

        BizException e = assertThrows(BizException.class, () -> service.register("zhangsan", "123456", "wx_zhangsan"));
        assertEquals("用户名已存在", e.getMessage());
    }

    @Test
    @DisplayName("注册成功：BCrypt 加密存储，默认普通用户且正常状态")
    void register_ok() {
        when(userMapper.findByUsername("newuser")).thenReturn(null);

        User u = service.register("newuser", "abc123", "wx_newuser");

        assertEquals(1, u.getRole());
        assertEquals(1, u.getStatus());
        // 数据库里存的是密文，明文不可比对
        assertEquals(false, u.getPassword().startsWith("abc123"));
        assertEquals(true, encoder.matches("abc123", u.getPassword()));
    }
}
