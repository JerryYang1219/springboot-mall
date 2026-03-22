package com.jerryyang.springbootmall.controller;

import com.jerryyang.springbootmall.dto.CreateOrderRequest;
import com.jerryyang.springbootmall.dto.OrderQueryParams;
import com.jerryyang.springbootmall.model.Order;
import com.jerryyang.springbootmall.service.OrderService;
import com.jerryyang.springbootmall.util.Page;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/users/{userId}/orders")
    public ResponseEntity<Page<Order>> getOrders(
            @PathVariable Integer userId,
            //@RequestParam 接收分頁參數。limit: 每一頁要幾筆數據 (預設 10 筆，最高 1000 筆)
            @RequestParam(defaultValue = "10") @Max(1000) @Min(0) Integer limit,
            //offset: 從第幾筆資料開始抓 (預設從 0 開始，即第一筆)
            @RequestParam(defaultValue = "0") @Min(0) Integer offset
    ){
        //建立參數封裝物件 (QueryParams)，將所有查詢條件 (誰、抓多少、從哪開始) 統整在一起
        OrderQueryParams orderQueryParams = new OrderQueryParams();
        orderQueryParams.setUserId(userId);
        orderQueryParams.setLimit(limit);
        orderQueryParams.setOffset(offset);

        //呼叫 Service 取得「該分頁」的訂單清單 (例如：第 1 到 10 筆)
        List<Order> orderList = orderService.getOrders(orderQueryParams);

        //呼叫 Service 取得該使用者的「總訂單數量」(用於前端計算總共有幾頁)
        Integer count = orderService.countOrder(orderQueryParams);

        //建立自定義的分頁包裝物件 (Page)，裝數據與分頁資訊
        Page<Order> page = new Page<>();
        page.setLimit(limit);
        page.setOffset(offset);
        page.setTotal(count);
        page.setResults(orderList);

        //回傳 HTTP 200 OK，並將完整的分頁資訊物件回傳給前端
        return ResponseEntity.status(HttpStatus.OK).body(page);
    }


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
