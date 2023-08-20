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


/**
 * 拦截器，验证是否登陆
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        // 用户可以访问员工的特定接口
        if (!StringUtils.hasText(token)) {
            token = request.getHeader("utoken");
            if (!request.getMethod().equals("GET")) {
                response.getWriter().println(JSON.toJSONString(Result.error("Beyond permissions！")));  // 用户只可以使用 get 请求
                return false;
            }
        }
        // 员工登陆
        try {
                Claims claims = JWTUtil.parser(token);
                Integer i = (Integer) claims.get("id");
                BaseContext.getThreadLocal().set(i.longValue());
                return true;
            } catch (Exception e) {
//                    e.printStackTrace();
        }

        response.getWriter().println(JSON.toJSONString(Result.error("NOT_LOGIN")));
        return false;
    }

}
