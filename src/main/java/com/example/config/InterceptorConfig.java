package com.example.config;


import com.example.interceptor.LoginInterceptor;
import com.example.interceptor.UserLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * 配置拦截器
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Autowired
    private UserLoginInterceptor userLoginInterceptor;

    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(loginInterceptor).addPathPatterns("/**").excludePathPatterns(
                "/error",
                "/login",
                "/user/**"
        );
        registry.addInterceptor(userLoginInterceptor).addPathPatterns("/user/**").excludePathPatterns(
                "/user/login",
                "/user/sendMsg"
        );
    }

}
