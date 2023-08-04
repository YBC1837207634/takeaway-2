package com.example.controller;


import com.example.common.Result;
import com.example.entity.Orders;
import com.example.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 提交订单
     * @param orders
     * @return
     */
    @PostMapping
    public Result<String> submit(@RequestBody Orders orders) {
        orderService.submit(orders);
        return Result.success("订单提交成功！");
    }
}
