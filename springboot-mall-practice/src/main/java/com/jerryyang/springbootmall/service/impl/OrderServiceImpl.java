package com.jerryyang.springbootmall.service.impl;

import com.jerryyang.springbootmall.dao.OrderDao;
import com.jerryyang.springbootmall.dao.ProductDao;
import com.jerryyang.springbootmall.dto.BuyItem;
import com.jerryyang.springbootmall.dto.CreateOrderRequest;
import com.jerryyang.springbootmall.model.Order;
import com.jerryyang.springbootmall.model.OrderItem;
import com.jerryyang.springbootmall.model.Product;
import com.jerryyang.springbootmall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDao orderDao;
    @Autowired
    private ProductDao productDao;

    @Override
    public Order getOrderById(Integer orderId) {
        //根據 orderId 去 order table 中查出這一筆訂單總資訊出來(userId、totalAmount)
        Order order = orderDao.getOrderById(orderId);

        //再去 order_item 表中查詢這筆訂單對應的「所有商品明細」。
        List<OrderItem> orderItemList = orderDao.getOrderItemByOrderId(orderId);

        //將查到的「明細清單」塞回「訂單物件」的變數中。
        order.setOrderItemList(orderItemList);

        return order;
    }

    @Transactional //操作多張資料表時必要註解，都成功or都失敗，避免資料庫不一致
    @Override
    public Integer createOrder(Integer userId, CreateOrderRequest createOrderRequest) {
        int totalAmount = 0;
        List<OrderItem> orderItemList = new ArrayList<>();

        //遍歷前端傳來的購買清單 (BuyItem)，逐一計算金額並轉換為訂單明細
        for(BuyItem buyItem : createOrderRequest.getBuyItemList()){
            Product product = productDao.getProductById(buyItem.getProductId());

            //計算總價錢 : 數量 * 單價
            int amount = buyItem.getQuantity() * product.getPrice();
            totalAmount = totalAmount + amount;

            //轉換 BuyItem (請求參數) to OrderItem (資料庫實體)
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(buyItem.getProductId());
            orderItem.setQuantity(buyItem.getQuantity());
            orderItem.setAmount(amount);

            // 加入清單，待會統一寫入資料庫
            orderItemList.add(orderItem);
        }

        // 創建訂單，並取得回傳 orderId
        Integer orderId = orderDao.createOrder(userId, totalAmount);

        //利用剛產生的 orderId，將所有明細項一次性存入 order_item 表
        orderDao.createOrderItems(orderId, orderItemList);

        //回傳訂單編號，供 Controller 層返回給前端
        return orderId;
    }
}
