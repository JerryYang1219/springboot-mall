package com.jerryyang.springbootmall.controller;

import com.jerryyang.springbootmall.dto.CreateOrderRequest;
import com.jerryyang.springbootmall.model.Order;
import com.jerryyang.springbootmall.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    //在眾多users帳號中/在這個userId底下/創造一筆訂單
    @PostMapping("/users/{userId}/orders")
    public ResponseEntity<?> createOrder(@PathVariable Integer userId,
                                         @RequestBody @Valid CreateOrderRequest createOrderRequest){
        //呼叫 Service 層處理建立訂單
        Integer orderId = orderService.createOrder(userId, createOrderRequest);

        //根據 orderId 查詢訂單數據
        Order order = orderService.getOrderById(orderId);

        //回傳 HTTP 201 Created，並在 Body 顯示訂單數據
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

}
