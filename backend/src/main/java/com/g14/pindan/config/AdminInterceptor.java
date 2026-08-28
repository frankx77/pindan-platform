package com.g14.pindan.config;

import com.g14.pindan.common.TokenStore;
import com.g14.pindan.entity.User;
import com.g14.pindan.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 管理员拦截器：校验登录用户是否 role=2（管理员）
 * 与 LoginInterceptor 叠加生效（LoginInterceptor 先通过，本拦截器再校验角色）
 */
@Component
public class AdminInterceptor implements HandlerInterceptor {

    private final TokenStore tokenStore;
    private final UserMapper userMapper;

    public AdminInterceptor(TokenStore tokenStore, UserMapper userMapper) {
        this.tokenStore = tokenStore;
        this.userMapper = userMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String token = request.getHeader("Authorization");
        Integer userId = tokenStore.getUserId(token);
        User user = userId == null ? null : userMapper.findById(userId);
        if (user == null || user.getRole() == null || user.getRole() != 2) {
            response.setStatus(403);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":403,\"msg\":\"无管理员权限\"}");
            return false;
        }
        request.setAttribute("currentUserId", userId);
        return true;
    }
}
