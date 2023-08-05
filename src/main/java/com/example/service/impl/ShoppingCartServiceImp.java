package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.ShoppingCart;
import com.example.exception.CommonException;
import com.example.mapper.ShoppingCartMapper;
import com.example.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImp extends ServiceImpl<ShoppingCartMapper, ShoppingCart>
        implements ShoppingCartService {
    @Override
    public ShoppingCart add(ShoppingCart shoppingCart) {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, shoppingCart.getUserId());

        // 判断添加的是套餐还是菜品
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();
        if (dishId == null && setmealId != null) {
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        } else if (dishId != null && setmealId == null) {
            queryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        } else {
            throw new CommonException("购物车信息格式错误！");
        }

        // 查找套餐或菜品
        ShoppingCart one = this.getOne(queryWrapper);
        // 没有找到就添加到购物车
        if (one == null) {
            this.save(shoppingCart);
        } else {
            one.setNumber(one.getNumber() + 1);  // 数量加一
            this.updateById(one);  // 更新
        }
        return this.getOne(queryWrapper);
    }
}
