package com.jerryyang.springbootmall.service.impl;

import com.jerryyang.springbootmall.dao.OrderDao;
import com.jerryyang.springbootmall.dao.ProductDao;
import com.jerryyang.springbootmall.dao.UserDao;
import com.jerryyang.springbootmall.dto.BuyItem;
import com.jerryyang.springbootmall.dto.CreateOrderRequest;
import com.jerryyang.springbootmall.dto.OrderQueryParams;
import com.jerryyang.springbootmall.model.Order;
import com.jerryyang.springbootmall.model.OrderItem;
import com.jerryyang.springbootmall.model.Product;
import com.jerryyang.springbootmall.model.User;
import com.jerryyang.springbootmall.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private UserDao userDao;

    private final static Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Override
    public Integer countOrder(OrderQueryParams orderQueryParams) {
        return orderDao.countOrder(orderQueryParams);
    }

    @Override
    public List<Order> getOrders(OrderQueryParams orderQueryParams) {
        //先從 DAO 取得「分頁後」的訂單主檔清單 (例如：第 1 到 10 筆訂單)
        // 此時拿到的 Order 物件中，orderItemList 欄位還是空的 (null)
        List<Order> orderList = orderDao.getOrders(orderQueryParams);

        //使用 for-each 迴圈，逐一處理清單中的每一筆訂單
        for(Order order : orderList){
            //根據每一筆訂單的 orderId，去 order_item 表中查出該訂單的所有商品明細
            List<OrderItem> orderItemList = orderDao.getOrderItemByOrderId(order.getOrderId());

            //將查到的明細清單塞回該筆訂單物件中，完成「主從資料聚合」
            order.setOrderItemList(orderItemList);
        }
        return orderList;
    }

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

    @Transactional //操作多張資料表時必要註解，都成功 or 都失敗，避免資料庫不一致
    @Override
    public Integer createOrder(Integer userId, CreateOrderRequest createOrderRequest) {
        //檢查user 是否存在
        User user = userDao.getUserById(userId);

        //如果不存在就噴出 400 BAD_REQUEST
        if(user == null){
            log.warn("此 userId {} 不存在", userId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        int totalAmount = 0;
        List<OrderItem> orderItemList = new ArrayList<>();

        //遍歷前端傳來的購買清單 (BuyItem)，逐一計算金額並轉換為訂單明細
        for(BuyItem buyItem : createOrderRequest.getBuyItemList()){
            Product product = productDao.getProductById(buyItem.getProductId());

            //檢查 product 是否存在、庫存是否足夠
            if(product == null){
                log.warn("商品 {} 不存在", buyItem.getProductId());
                throw  new ResponseStatusException(HttpStatus.BAD_REQUEST);

            } else if (product.getStock() < buyItem.getQuantity()) {
                log.warn("商品 {} 庫存數量不足，無法購買。剩餘庫存 {}，欲購買數量 {} ",
                        buyItem.getProductId(), product.getStock(), buyItem.getQuantity());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }

            //扣除商品庫存
            productDao.updateStock(product.getProductId(), product.getStock() - buyItem.getQuantity());

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
