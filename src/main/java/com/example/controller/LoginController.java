package com.example.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.common.Result;
import com.example.entity.Employee;
import com.example.service.EmployeeService;
import com.example.utils.JWTUtil;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


/**
 *
 * 登陆验证
 */
@Slf4j
@RestController
public class LoginController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public Result<String> login(Employee employee) {

        Long id = Long.valueOf(employeeService.isLogin(employee));
        if (id != -1) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("username", employee.getUsername());
            claims.put("id", id);
            String token = JWTUtil.produce(claims);
            return Result.success(token);
        }
        return Result.error("账号或密码错误");
    }


}
