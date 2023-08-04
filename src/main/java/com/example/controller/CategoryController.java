package com.example.controller;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.Result;
import com.example.entity.Category;
import com.example.service.CategoryService;
import com.example.service.DishService;
import com.example.service.SetmealService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/category")
public class CategoryController {

    private CategoryService categoryService;

    private DishService dishService;

    private SetmealService setmealService;

    public CategoryController(CategoryService categoryService, DishService dishService, SetmealService setmealService) {
        this.categoryService = categoryService;
        this.dishService = dishService;
        this.setmealService = setmealService;
    }

    /**
     * 新增菜品
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody Category category) {
        boolean save = categoryService.save(category);
        if (!save) return Result.error("添加失败！");
        return Result.success("添加成功");
    }

    /**
     * 分页查询
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("/page")
    public Result<IPage<Category>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit) {
        IPage<Category> p = new Page<>(page, limit);
        QueryWrapper<Category> qw = new QueryWrapper<>();
        qw.orderByAsc("sort");
        categoryService.page(p, qw);
        return Result.success(p);
    }


    /**
     * /list?type=?
     * 根据条件返回列表
     * @param category
     * @return
     */
    @GetMapping("/list")
    public Result<List<Category>> listCondition(Category category) {

        // 根据type字段来返回结果
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(category.getType() != null, Category::getType, category.getType());
        List<Category> list = categoryService.list(wrapper);
        return Result.success(list);
    }

    /**
     * 删除菜品或者套餐的类别
     * @param category
     * @return
     */
    @DeleteMapping
    public Result<String> delete(Category category) {
        boolean b = categoryService.removeCategory(category);
        if (b) return Result.success("删除成功");
        return Result.error("删除失败！");
    }

    @PutMapping
    public Result<String> update(@RequestBody  Category category) {
        boolean b = categoryService.updateById(category);
        if (!b) return Result.success("修改失败！");
        return  Result.success("修改成功！");

    }

}
