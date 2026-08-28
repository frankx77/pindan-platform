package com.g14.pindan.service;

import com.g14.pindan.common.BizException;
import com.g14.pindan.entity.User;
import com.g14.pindan.mapper.UserMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户业务逻辑
 */
@Service
public class UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 注册：用户名唯一校验 + BCrypt 加密
     */
    public User register(String username, String password, String wechat) {
        // 用户名唯一性校验
        if (userMapper.findByUsername(username) != null) {
            throw new BizException("用户名已存在");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(username);
        user.setWechat(wechat);
        user.setRole(1);
        user.setStatus(1);
        userMapper.insert(user);
        return user;
    }

    /**
     * 登录校验：返回用户或抛异常
     */
    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new BizException("用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BizException("账号已被禁用，请联系管理员");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BizException("用户名或密码错误");
        }
        return user;
    }

    public User findById(Integer id) {
        return userMapper.findById(id);
    }
}
