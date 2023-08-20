package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dto.DishDto;
import com.example.entity.Dish;

import java.util.List;


public interface DishService extends IService<Dish> {

    /**
     *
     * @param dishDto
     */
    boolean saveDish(DishDto dishDto);

    DishDto getDishById(Long id);


    boolean updateDish(DishDto ds);

    boolean deleteDishByIds(List<Long> ids);
}
