package com.jerryyang.springbootmall.service;

import com.jerryyang.springbootmall.dto.CreateOrderRequest;
import com.jerryyang.springbootmall.dto.OrderQueryParams;
import com.jerryyang.springbootmall.model.Order;

import java.util.List;

public interface OrderService {

    Integer countOrder(OrderQueryParams orderQueryParams);

    List<Order> getOrders(OrderQueryParams orderQueryParams);

    Order getOrderById(Integer orderId);

    Integer createOrder(Integer userId, CreateOrderRequest createOrderRequest);
}
