package com.g14.pindan.common;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简单 Token 管理（内存版）
 * 登录生成 UUID token → userId 映射；后续阶段可升级 JWT。
 */
@Component
public class TokenStore {

    private final Map<String, Integer> tokenToUserId = new ConcurrentHashMap<>();

    /**
     * 创建 token 并绑定用户
     */
    public String create(Integer userId) {
        String token = UUID.randomUUID().toString().replace("-", "");
        tokenToUserId.put(token, userId);
        return token;
    }

    /**
     * 根据 token 获取用户ID，无效返回 null
     */
    public Integer getUserId(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        return tokenToUserId.get(token);
    }

    public void remove(String token) {
        if (token != null) {
            tokenToUserId.remove(token);
        }
    }
}
