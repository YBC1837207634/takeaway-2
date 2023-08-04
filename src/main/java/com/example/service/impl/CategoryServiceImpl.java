package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Category;
import com.example.entity.Dish;
import com.example.entity.Setmeal;
import com.example.exception.DeleteException;
import com.example.mapper.CategoryMapper;
import com.example.service.CategoryService;
import com.example.service.DishService;
import com.example.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    private DishService dishService;

    private SetmealService setmealService;

    @Autowired
    public CategoryServiceImpl(DishService dishService, SetmealService setmealService) {
        this.dishService = dishService;
        this.setmealService = setmealService;
    }

    /**
     * 删除指定的类别根据id只有当该类别没有和别的表建立关联才会删除类别
     * @param category
     * @return
     */
    @Override
    public boolean removeCategory(Category category) {
        Category result = super.getById(category.getId());
        if (result == null) {
            return false;
        }
        // 需要删除菜品的类别
        if (result.getType() == 1) {
            // 根据菜品类别来查询菜品表中是否还有该类别的菜品
            LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Dish::getCategoryId, category.getId());
            long count = dishService.count(lambdaQueryWrapper);
            if (count > 0) {
                throw new DeleteException("该类别下绑定了菜品，无法删除");
            }
        } else if (result.getType() == 2) {
            // 删除套餐的类别
            LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Setmeal::getCategoryId, category.getId());
            long count = setmealService.count(lambdaQueryWrapper);
            if (count > 0) {
                throw new DeleteException("该类别下绑定了套餐，无法删除");
            }
        }

        return super.removeById(category);

    }
}
