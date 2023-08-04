package com.example.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.common.BaseContext;
import com.example.common.Result;
import com.example.entity.AddressBook;
import com.example.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/user/address")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    @PostMapping
    public Result<String> save(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getThreadLocal().get());   // 设置用户id
        if (addressBookService.save(addressBook)) return Result.success("地址添加成功！");
        return Result.error("地址添加失败！");
    }

    @DeleteMapping
    public Result<String> delete(AddressBook addressBook) {
        LambdaQueryWrapper<AddressBook> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AddressBook::getId, addressBook.getId());
        lambdaQueryWrapper.eq(AddressBook::getUserId,BaseContext.getThreadLocal().get());
        if (addressBookService.remove(lambdaQueryWrapper))
            return Result.success("地址删除成功！");
        return Result.error("地址删除失败！");
    }

    /**
     * 设置默认地址
     * @return
     */
    @PutMapping("/default")
    public Result<String> setDefault(@RequestBody AddressBook addressBook) {

        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AddressBook::getUserId, BaseContext.getThreadLocal().get());  // 修改所有与该用户相关的地址簿
        updateWrapper.set(AddressBook::getIsDefault, 0);  // 全部置为非默认
//        addressBook.setIsDefault(1);  // 设置为默认
        addressBookService.update(updateWrapper);

        updateWrapper.clear();
        updateWrapper.set(AddressBook::getIsDefault, 1);
        updateWrapper.eq(AddressBook::getId, addressBook.getId());
        updateWrapper.eq(AddressBook::getUserId, BaseContext.getThreadLocal().get());   // 改地址必须时该用户的地址
        if (addressBookService.update(updateWrapper))
            return Result.success("设置默认成功！");
        return Result.error("设置失败！");
    }

    /**
     * 更新地址
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody AddressBook addressBook) {
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressBook::getId, addressBook.getId());
        wrapper.eq(AddressBook::getUserId, BaseContext.getThreadLocal().get());
        if (addressBookService.update(addressBook, wrapper))
            return Result.success("更新成功！");
        return Result.error("地址更新失败！");
    }

    @GetMapping
    public Result<List<AddressBook>> list() {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getThreadLocal().get());
        List<AddressBook> list = addressBookService.list(queryWrapper);
        return Result.success(list);

    }

    /**
     * 返回默认地址
     * @return
     */
    @GetMapping("/default")
    public Result<AddressBook> getDefault() {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getThreadLocal().get());
        queryWrapper.eq(AddressBook::getIsDefault, 1);   // 默认地址
        return Result.success(addressBookService.getOne(queryWrapper));
    }

}
