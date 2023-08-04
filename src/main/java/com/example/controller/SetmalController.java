package com.example.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.Result;
import com.example.dto.SetmealDto;
import com.example.entity.Category;
import com.example.entity.Dish;
import com.example.entity.Setmeal;
import com.example.entity.SetmealDish;
import com.example.service.CategoryService;
import com.example.service.SetmealDishService;
import com.example.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/setmeal")
public class SetmalController {

    private SetmealService setmealService;

    private SetmealDishService setmealDishService;

    private CategoryService categoryService;

    @Autowired
    public SetmalController(SetmealService setmealService, SetmealDishService setmealDishService, CategoryService categoryService) {
        this.setmealService = setmealService;
        this.setmealDishService = setmealDishService;
        this.categoryService = categoryService;
    }

    /**
     * 按名称分页 额外提供套餐名称
     * @param page
     * @param limit
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page<SetmealDto>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit, String name) {

        // 套餐分页结果
        Page<Setmeal> p = new Page<>(page, limit);
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name!=null, Setmeal::getName, name);   // 模糊匹配
        setmealService.page(p, wrapper);

        // 在分页结果中包含套餐所包含的菜品
        Page<SetmealDto> newPage = new Page<>();
        BeanUtils.copyProperties(p, newPage, "records");  // 拷贝除结果列表的其他字段
        List<SetmealDto> setmealDtos = p.getRecords().stream().map(item -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Category result = categoryService.getById(item.getCategoryId());

            if (result != null) {
                setmealDto.setCategoryName(result.getName());
            }
//            LambdaQueryWrapper<SetmealDish> wp = new LambdaQueryWrapper<>();
//            wp.eq(SetmealDish::getSetmealId, item.getId());
//            List<SetmealDish> list = setmealDishService.list(wp);
//            // 携带菜品列表
//            setmealDto.setDishes(list);
            // 携带套餐名称

            return setmealDto;
        }).toList();

        newPage.setRecords(setmealDtos);
        return Result.success(newPage);
    }

    @GetMapping
    public Result<List<SetmealDto>> list(Setmeal setmeal) {

        // 查询套餐
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(setmeal.getStatus()!=null, Setmeal::getStatus, setmeal.getStatus());
        wrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId, setmeal.getCategoryId());
        List<Setmeal> list = setmealService.list(wrapper);

        // 携带菜品
        List<SetmealDto> newList = list.stream().map(item -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Category result = categoryService.getById(item.getCategoryId());
            if (result != null) setmealDto.setCategoryName(result.getName());
            // 套餐关联的菜品
            LambdaQueryWrapper<SetmealDish> wp = new LambdaQueryWrapper<>();
            wp.eq(SetmealDish::getSetmealId, item.getId());
            List<SetmealDish> list1 = setmealDishService.list(wp);
            setmealDto.setDishes(list1);
            return setmealDto;
        }).toList();

        return Result.success(newList);
    }

    @GetMapping("/{id}")
    public Result<SetmealDto> get(@PathVariable Long id) {
        SetmealDto r = setmealService.getById(id);
        return Result.success(r);
    }

    @PostMapping
    public Result<String> save(@RequestBody SetmealDto setmealDto) {
        if (setmealService.saveSetmeal(setmealDto)) return Result.success("添加成功！");
        return Result.error("添加失败！");
    }

    @DeleteMapping
    public Result<String> delete(@RequestParam List<Long> ids) {
        if (setmealService.deleteSetmealByIds(ids))
            return Result.success("删除成功");

        return Result.error("删除失败！");
    }
}
