package com.jerryyang.springbootmall.dao;

import com.jerryyang.springbootmall.dto.OrderQueryParams;
import com.jerryyang.springbootmall.model.Order;
import com.jerryyang.springbootmall.model.OrderItem;

import java.util.List;

public interface OrderDao {

    Integer countOrder(OrderQueryParams orderQueryParams);

    List<Order> getOrders(OrderQueryParams orderQueryParams);

    Order getOrderById(Integer orderId);

    List<OrderItem> getOrderItemByOrderId(Integer orderId);

    Integer createOrder(Integer user, Integer totalAmount);

    void createOrderItems(Integer orderId, List<OrderItem> orderItemList);
}
