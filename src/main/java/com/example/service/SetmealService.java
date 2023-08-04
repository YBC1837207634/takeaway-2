package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dto.SetmealDto;
import com.example.entity.Setmeal;
import java.util.List;


public interface SetmealService extends IService<Setmeal> {

    SetmealDto getById(Long id);

    boolean saveSetmeal(SetmealDto setmealDto);

    boolean deleteSetmealByIds(List<Long> ids);

}
