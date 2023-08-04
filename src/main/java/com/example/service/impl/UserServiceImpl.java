package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.Result;
import com.example.entity.User;
import com.example.mapper.UserMapper;
import com.example.service.UserService;
import com.example.utils.JWTUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 登陆 注册
     * @param user
     * @return
     */
    @Override
    public User registerLogin(User user) {
        // 验证成功，查询是否有该用户如果没有将该用户存入数据库。
        String phone = user.getPhone();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        User one = super.getOne(wrapper);
        if (one == null) {
            one = new User();
            one.setPhone(phone);
            one.setStatus(1);
            super.save(one);
        }
        // 生成 jwt 令牌
        return one;
    }
}
