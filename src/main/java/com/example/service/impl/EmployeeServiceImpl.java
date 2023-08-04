package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Employee;
import com.example.mapper.EmployeeMapper;
import com.example.service.EmployeeService;
import com.example.utils.JWTUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Map;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    private EmployeeMapper emp;

    public EmployeeServiceImpl(EmployeeMapper emp) {
        this.emp = emp;
    }

    public long isLogin(Employee employee) {
        if (employee.getUsername() == null) return -1;
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getUsername, employee.getUsername());
        Employee result = emp.selectOne(wrapper);
        if (result.getStatus() == 0) return -1;
        String password = DigestUtils.md5DigestAsHex(employee.getPassword().getBytes());
        if (result.getUsername() != null && result.getPassword().equals(password)) {
            return result.getId();
        }
        return -1;
    }
}
