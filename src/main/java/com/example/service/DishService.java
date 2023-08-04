package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dto.DishDto;
import com.example.entity.Dish;


public interface DishService extends IService<Dish> {

    /**
     *
     * @param dishDto
     */
    boolean saveDish(DishDto dishDto);

    DishDto getDishById(Long id);


    boolean updateDish(DishDto ds);
}
