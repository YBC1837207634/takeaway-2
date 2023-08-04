package com.example.dto;


import com.example.entity.Setmeal;
import com.example.entity.SetmealDish;
import lombok.Data;

import java.util.List;


/**
 * 套餐 + 菜品
 */
@Data
public class SetmealDto extends Setmeal {
    private List<SetmealDish> dishes;    // 菜品列表
    private String categoryName;  // 类别名
}
