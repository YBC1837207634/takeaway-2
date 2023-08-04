package com.example.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.common.BaseContext;
import com.example.common.Result;
import com.example.entity.ShoppingCart;
import com.example.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shopping")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping
    public Result<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        shoppingCart.setUserId(BaseContext.getThreadLocal().get());  // 哪一个用户添加的
       ShoppingCart result = shoppingCartService.add(shoppingCart);
        return Result.success(result);
    }

    @RequestMapping
    public Result<List<ShoppingCart>> list() {
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, BaseContext.getThreadLocal().get());
        wrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(wrapper);
        return Result.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping
    public Result<String> clear() {
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, BaseContext.getThreadLocal().get());
        shoppingCartService.remove(wrapper);
        return Result.success("清空成功！");
    }

}
