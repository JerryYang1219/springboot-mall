package com.jerryyang.springbootmall.service;

import com.jerryyang.springbootmall.dto.CreateOrderRequest;
import com.jerryyang.springbootmall.model.Order;

public interface OrderService {

    Order getOrderById(Integer orderId);

    Integer createOrder(Integer userId, CreateOrderRequest createOrderRequest);
}
