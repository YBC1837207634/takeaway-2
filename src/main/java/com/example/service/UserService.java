package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.User;

import java.util.Map;

public interface UserService extends IService<User> {


    User registerLogin(User user);
}
