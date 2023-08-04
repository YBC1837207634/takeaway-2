package com.example.dto;


import com.example.entity.Dish;
import com.example.entity.DishFlavor;
import lombok.Data;

import java.util.List;

/**
 *  数据传输对象（DTO）
 * 对 Dish 添加额外的描述字段
 */
@Data
public class DishDto extends Dish {

    // 保存菜品口味
    private List<DishFlavor> flavors;

    // 对应菜品的类别名称
    private String CategoryName;

}
