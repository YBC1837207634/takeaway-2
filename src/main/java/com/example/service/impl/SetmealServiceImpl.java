package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dto.SetmealDto;
import com.example.entity.Category;
import com.example.entity.Setmeal;
import com.example.entity.SetmealDish;
import com.example.exception.DeleteException;
import com.example.mapper.CategoryMapper;
import com.example.mapper.SetmealMapper;
import com.example.service.SetmealDishService;
import com.example.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 根据id查询结果，封装套餐 + 菜品
     * @param id
     * @return
     */
    @Override
    public SetmealDto getById(Long id) {

        // 套餐信息
        Setmeal result = super.getById(id);

        // 菜品信息
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> list = setmealDishService.list(wrapper);

        // 套餐和菜品封装 返回
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(result, setmealDto);
        setmealDto.setDishes(list);
        // 套餐种类名称
        Category category = categoryMapper.selectById(setmealDto.getCategoryId());
        if (category != null)
            setmealDto.setCategoryName(category.getName());

        return setmealDto;
    }


    /**
     * 保存套餐信息 通过 DTO 类，额外包含菜品信息
     * @param setmealDto
     * @return
     */
    @Override
    @Transactional
    public boolean saveSetmeal(SetmealDto setmealDto) {
        boolean b = super.save(setmealDto);// 保存套餐
        // 保存所关联的菜品
        setmealDto.getDishes().forEach(item -> {
            item.setSetmealId(setmealDto.getId());
        });
        boolean b1 = setmealDishService.saveBatch(setmealDto.getDishes());
        return b&&b1;
    }

    @Override
    public boolean deleteSetmealByIds(List<Long> ids) {

        // 根据id批量删除
//        boolean b = super.removeByIds(ids);
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);  // id in (ids)
        queryWrapper.eq(Setmeal::getStatus, 1);  // 是否启用
        // 不可以删除正在售卖的套餐，如果启用抛出异常。
        long count = super.count(queryWrapper);
        queryWrapper.clear();
        if (count > 0) {
            throw new DeleteException("套餐正在售卖，不能删除");
        } else {
            queryWrapper.in(Setmeal::getId, ids);  // id in (ids)
            queryWrapper.eq(Setmeal::getStatus, 0);  // 是否启用
            super.remove(queryWrapper);
        }

        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(wrapper);

        return true;
    }
}
