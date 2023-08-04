package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dto.DishDto;
import com.example.entity.Dish;
import com.example.entity.DishFlavor;
import com.example.mapper.DishMapper;
import com.example.service.DishFlavorService;
import com.example.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 菜品表与口味表关联
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    private DishFlavorService dishFlavorService;

    public DishServiceImpl(DishFlavorService dishFlavorService) {
        this.dishFlavorService = dishFlavorService;

    }

    /**
     * 保存菜品，需要提供口味
     *
     * @param dishDto
     */
    @Override
    @Transactional
    public boolean saveDish(DishDto dishDto) {
        // 保存菜品的基础信息
        super.save(dishDto);
        long id = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.forEach(item -> {
            item.setDishId(id);
        });
        // 建立菜品表和口味表的联系
        return dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据菜品 id 查询菜品，查询口味，封装在一起返回
     * @param id
     * @return 菜品+口味
     */
    @Override
    public DishDto getDishById(Long id) {
        DishDto dishDto = new DishDto();
        // 查找菜品
        Dish result = super.getById(id);
        if (result == null) {
            return dishDto;
        }
        BeanUtils.copyProperties(result, dishDto);
        // 菜品对应的口味有哪些
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> list = dishFlavorService.list(wrapper);
        dishDto.setFlavors(list);
        return dishDto;
    }

    /**
     * 更新菜品和口味
     * @return
     */
    @Override
    @Transactional
    public boolean updateDish(DishDto ds) {
        // 更新菜品
        boolean b1 = super.updateById(ds);
        Long id = ds.getId();  // 获取id 用来与口味表联系
        //更新口味
        // 删除菜品对应的所有口味
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId, ds.getId());
        dishFlavorService.remove(wrapper);
        // 添加口味
        ds.getFlavors().forEach(item -> {
            item.setDishId(id);
        });
        boolean b = dishFlavorService.saveBatch(ds.getFlavors());
        return b1 && b;

    }


}
