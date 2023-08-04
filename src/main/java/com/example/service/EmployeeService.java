package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.Employee;

import java.util.Map;

public interface EmployeeService extends IService<Employee> {

    /**
     * 登陆验证，登陆成功返回用户id 否则 -1
     * @param employee
     * @return
     */
    long isLogin(Employee employee);
}
