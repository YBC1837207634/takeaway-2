package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.Result;
import com.example.dto.SetmealDto;
import com.example.entity.Category;
import com.example.entity.Setmeal;
import com.example.entity.SetmealDish;
import com.example.service.CategoryService;
import com.example.service.SetmealDishService;
import com.example.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    // 在方法执行前查看是否有缓存对应的数据，如果有直接返回数据，如果没有调用方法获取数据返回，并缓存起来。
    @GetMapping("/page")
    @Cacheable(value = "setmealCache", key = "'setmeal_' + #page + '_' + #limit", unless = "#result.data.isEmpty()")
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
            return setmealDto;
        }).toList();

        newPage.setRecords(setmealDtos);
        return Result.success(newPage);
    }

    @GetMapping
    @Cacheable(
        cacheNames = "setmealCache",
        key = "'setmeal_' + #setmeal.categoryId + #setmeal.status",
        unless = "#result.data.isEmpty()"
    )
    public Result<List<SetmealDto>> list(Setmeal setmeal) {

        // 查询套餐
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(setmeal.getStatus()!=null, Setmeal::getStatus, setmeal.getStatus());
        wrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId, setmeal.getCategoryId());
        wrapper.eq(setmeal.getStatus()!=null, Setmeal::getStatus, setmeal.getStatus());   // 状态
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

    /**
     * @param setmealDto
     * @return
     */
    @PostMapping
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    public Result<String> save(@RequestBody SetmealDto setmealDto) {
        if (setmealService.saveSetmeal(setmealDto)) return Result.success("添加成功！");
        return Result.error("添加失败！");
    }

    /**
     * 删除操作清理缓存
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)  // 清理 setmealCache 下的所有缓存数据
    public Result<String> delete(@RequestParam List<Long> ids) {
        if (setmealService.deleteSetmealByIds(ids))
            return Result.success("删除成功");

        return Result.error("删除失败！");
    }
}
