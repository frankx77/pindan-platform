package com.g14.pindan.controller;

import com.g14.pindan.common.Result;
import com.g14.pindan.common.TokenStore;
import com.g14.pindan.entity.User;
import com.g14.pindan.service.UserService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户认证接口
 */
@RestController
@RequestMapping("/api/user")
@Validated
public class UserController {

    private final UserService userService;
    private final TokenStore tokenStore;

    public UserController(UserService userService, TokenStore tokenStore) {
        this.userService = userService;
        this.tokenStore = tokenStore;
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result<User> register(@RequestBody @Validated RegisterDTO dto) {
        User user = userService.register(dto.getUsername(), dto.getPassword(), dto.getWechat());
        return Result.success(user);
    }

    /**
     * 登录：返回 token + 用户信息
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody @Validated LoginDTO dto) {
        User user = userService.login(dto.getUsername(), dto.getPassword());
        String token = tokenStore.create(user.getId());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);
        return Result.success(data);
    }

    @Data
    public static class RegisterDTO {
        @NotBlank(message = "用户名不能为空")
        @Size(min = 3, max = 20, message = "用户名长度需在 3~20 之间")
        private String username;

        @NotBlank(message = "密码不能为空")
        @Size(min = 6, max = 30, message = "密码长度需在 6~30 之间")
        private String password;

        @NotBlank(message = "请填写微信号")
        @Size(max = 50, message = "微信号不能超过 50 个字符")
        private String wechat;
    }

    @Data
    public static class LoginDTO {
        @NotBlank(message = "用户名不能为空")
        private String username;

        @NotBlank(message = "密码不能为空")
        private String password;
    }
}
