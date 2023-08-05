package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.BaseContext;
import com.example.entity.*;
import com.example.exception.CommonException;
import com.example.mapper.*;
import com.example.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Transactional
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders>
    implements OrderService {

    private AddressBookMapper addressBookMapper;

    private ShoppingCartMapper shoppingCartMapper;

    private UserMapper userMapper;

    private OrderDetailMapper orderDetailMapper;


    @Autowired
    public OrderServiceImpl(AddressBookMapper addressBookMapper, ShoppingCartMapper shoppingCartMapper, UserMapper userMapper, OrderDetailMapper orderDetailMapper) {
        this.addressBookMapper = addressBookMapper;
        this.shoppingCartMapper = shoppingCartMapper;
        this.userMapper = userMapper;
        this.orderDetailMapper = orderDetailMapper;
    }

    /**
     * 保存订单
     * @param orders
     * @return
     */
    @Override
    public void submit(Orders orders) {
        Long code = IdWorker.getId();  // 生成订单号
        Long userId = BaseContext.getThreadLocal().get();

        // 地址簿信息
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressBook::getUserId, userId);
        wrapper.eq(AddressBook::getIsDefault, 1);   // 默认地址
        AddressBook defaultAddressBook = addressBookMapper.selectOne(wrapper);

        // 购物车内所有商品
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectList(shoppingCartLambdaQueryWrapper);
        if (shoppingCarts == null || shoppingCarts.size() == 0) {
            throw new CommonException("订单异常:购物车为空");
        }

        // 计算总金额
        AtomicInteger sum = new AtomicInteger(0);
        // 订单详细
        List<OrderDetail> orderDetails = shoppingCarts.stream().map(item -> {
            // 订单详情表
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setName(item.getName());  // 套餐或者菜品姓名
            orderDetail.setImage(item.getImage());
            orderDetail.setOrderId(item.getId()); // 订单id
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setNumber(item.getNumber());  // 商品数量
            orderDetail.setAmount(item.getAmount());  // 单个价格
            sum.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).toList();

        // 用户信息
        User user = userMapper.selectById(userId);

        // 补全订单字段
        orders.setNumber(code.toString());
        orders.setUserId(userId);
        orders.setAddressBookId(defaultAddressBook.getId());
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setAmount(new BigDecimal(sum.intValue()));   // 订单总金额
        orders.setPhone(defaultAddressBook.getPhone());
        orders.setAddress(defaultAddressBook.address());
        orders.setUserName(user.getName());
        orders.setConsignee(defaultAddressBook.getConsignee());   // 收获人

        // 插入数据
        this.save(orders);  // 保存订单信息
        orderDetails.forEach(item -> {
            orderDetailMapper.insert(item);
        });

        // 清空购物车
        shoppingCartMapper.delete(shoppingCartLambdaQueryWrapper);
    }
}
