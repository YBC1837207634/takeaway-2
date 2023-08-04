package com.example.interceptor;

import com.alibaba.fastjson2.JSON;
import com.example.common.BaseContext;
import com.example.common.Result;
import com.example.utils.JWTUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
public class UserLoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String utoken = request.getHeader("utoken");
        // 用户登陆
        if (StringUtils.hasText(utoken)) {
            try {
                Claims claims = JWTUtil.parser(utoken);
                Integer id = (Integer) claims.get("id");
                BaseContext.getThreadLocal().set(id.longValue());  // 保存到线程变量中
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        response.getWriter().println(JSON.toJSONString(Result.error("user not login")));
        return false;
    }
}
