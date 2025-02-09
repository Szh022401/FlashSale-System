package com.ecommerce.flashsale.db.dao;

import com.ecommerce.flashsale.db.po.Order;

public interface OrderDao {
    void insertOrder(Order order);
    Order queryOrders(String OrderNo) ;
    void updateOrder(Order order);

}
