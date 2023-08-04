package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.Result;
import com.example.entity.ShoppingCart;


public interface ShoppingCartService extends IService<ShoppingCart> {

    public ShoppingCart add(ShoppingCart shoppingCart);
}
