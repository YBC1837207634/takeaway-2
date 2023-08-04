package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.Category;

public interface CategoryService extends IService<Category> {

    /**
     * 删除类别。
     * 删除条件：如果当前类别绑定了菜品或者套餐就不会删除
     * @param category
     */
    boolean removeCategory(Category category);
}
