package com.g14.pindan.config;

import com.g14.pindan.common.TokenStore;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录拦截器：校验请求头 Authorization 中的 token
 * 未登录返回 401；放行路径在 WebConfig 中配置
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    private final TokenStore tokenStore;

    public LoginInterceptor(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 OPTIONS 预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String token = request.getHeader("Authorization");
        Integer userId = tokenStore.getUserId(token);
        if (userId == null) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"未登录或登录已过期\"}");
            return false;
        }
        // 将当前用户ID存入 request 属性，供 Controller/Service 使用
        request.setAttribute("currentUserId", userId);
        return true;
    }
}
