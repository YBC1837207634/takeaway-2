package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.Result;
import com.example.dto.DishDto;
import com.example.entity.Category;
import com.example.entity.Dish;
import com.example.entity.DishFlavor;
import com.example.service.CategoryService;
import com.example.service.DishFlavorService;
import com.example.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 添加菜品，需要多表操作，控制层还需要接收到前端传来的口味数据，需要使用扩展实体类接受数据，DTO
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody DishDto dishDto) {
        boolean b = dishService.saveDish(dishDto);
        if (!b) return Result.error("菜品添加失败！");
        return Result.success("菜品添加成功！");
    }

    /**
     * 根据菜品类别查找菜品，携带口味信息
     * 使用 redis 缓存
     * @param dish
     * @return
     */
    @GetMapping
    public Result<List<DishDto>> list(Dish dish) {
        String key = "dish_categoryId_" + dish.getCategoryId();
        List<DishDto> dishDtos = (List<DishDto>) redisTemplate.opsForValue().get(key);

        // 如果 redis 中有缓存 直接返回
        if (dishDtos != null) {
            return Result.success(dishDtos);
        }
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        lambdaQueryWrapper.eq(dish.getStatus() != null, Dish::getStatus, dish.getStatus());
        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(lambdaQueryWrapper);
        // 携带菜品口味
        dishDtos = list.stream().map(item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto, "records");
            LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DishFlavor::getDishId, item.getId());
            dishDto.setFlavors(dishFlavorService.list(wrapper));
            Category r = categoryService.getById(item.getCategoryId());
            if (r != null) dishDto.setCategoryName(r.getName());
            return dishDto;
        }).toList();
        // 缓存到 redis
        redisTemplate.opsForValue().set(key, dishDtos, 60, TimeUnit.MINUTES);   // 60 分钟
        return Result.success(dishDtos);
    }

    /**
     * 返回菜品列表，如果提供了名称，则按照名称返回，返回的结果需要将菜品对应的类别名称一并返回。
     * @param page
     * @param limit
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page<DishDto>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit, String name) {
        Page<Dish> p = new Page<>(page, limit);
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null, Dish::getName, name);  // 如果提供name则增加该条件
        dishService.page(p, wrapper);
        Page<DishDto> newPage = new Page<>();
        BeanUtils.copyProperties(p, newPage, "records");
        List<Dish> temp = p.getRecords();
        List<DishDto> list = temp.stream().map(item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Category r = categoryService.getById(item.getCategoryId());
            if (r != null) dishDto.setCategoryName(r.getName());
            return dishDto;
        }).toList();
        newPage.setRecords(list);
        return Result.success(newPage);
    }

    /**
     * 根据 id 查询菜品，返回对象内部封装 DishDTO，包含菜品数表据和口味表
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishService.getDishById(id);
        return Result.success(dishDto);
    }

    /**
     * 修改菜品信息 包含口味
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody DishDto dishDto) {
        boolean b = dishService.updateDish(dishDto);
        if (b) {
            String key = "dish_categoryId_" + dishDto.getCategoryId();
            redisTemplate.delete(key);
            return Result.success("菜品修改成功！") ;
        }
        return Result.error("菜品修改失败！");
    }

}
