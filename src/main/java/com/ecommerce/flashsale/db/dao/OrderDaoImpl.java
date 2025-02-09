package com.ecommerce.flashsale.db.dao;

import com.ecommerce.flashsale.db.mappers.OrderMapper;
import com.ecommerce.flashsale.db.po.Order;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class OrderDaoImpl implements OrderDao {
    @Resource
    private OrderMapper orderMapper;

    @Override
    public void insertOrder(Order order) {
        orderMapper.insert(order);
    }

    @Override
    public Order queryOrders(String OrderNo) {
        return orderMapper.selectByOrderNo(OrderNo);
    }

    @Override
    public void updateOrder(Order order) {
        orderMapper.updateByPrimaryKey(order);
    }
}
