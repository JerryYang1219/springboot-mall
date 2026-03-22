package com.jerryyang.springbootmall.dao.impl;


import com.jerryyang.springbootmall.dao.OrderDao;
import com.jerryyang.springbootmall.dto.OrderQueryParams;
import com.jerryyang.springbootmall.model.Order;
import com.jerryyang.springbootmall.model.OrderItem;
import com.jerryyang.springbootmall.rowmapper.OrderItemRowMapper;
import com.jerryyang.springbootmall.rowmapper.OrderRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OrderDaoImpl implements OrderDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Integer countOrder(OrderQueryParams orderQueryParams) {
        //定義 SQL 聚合函數 count(*)
        String sql = "SELECT count(*) FROM `order` WHERE 1=1";

        //建立參數 Map
        Map<String, Object> map = new HashMap<>();

        sql = addFilteringSql(sql, map, orderQueryParams);

        //執行查詢。queryForObject 用於回傳單一結果（如數字、字串）。
        Integer total = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);

        return total;
    }

    @Override
    public List<Order> getOrders(OrderQueryParams orderQueryParams) {
        //SQL 語句：查詢訂單主表。
        String sql = "SELECT order_id, user_id, total_amount, created_date, last_modified_date FROM `order` WHERE 1=1";

        Map<String, Object> map = new HashMap<>();

        sql = addFilteringSql(sql, map, orderQueryParams);

        //設定為「最新訂單排在最前面」(DESC)
        sql = sql + " ORDER BY created_date DESC";

        //加入 LIMIT 與 OFFSET，:limit 代表每一頁抓幾筆，:offset 代表從第幾筆開始跳過
        sql = sql + " LIMIT :limit OFFSET :offset";
        map.put("limit", orderQueryParams.getLimit());
        map.put("offset", orderQueryParams.getOffset());

        List<Order> orderList = namedParameterJdbcTemplate.query(sql, map, new OrderRowMapper());

        return orderList;
    }

    @Override
    public Order getOrderById(Integer orderId) {
        //定義 SQL 查詢語句
        String sql = "SELECT order_id, user_id, total_amount, created_date, last_modified_date " +
                "FROM `order` WHERE order_id = :order_id";

        //建立參數 Map
        Map<String, Object> map = new HashMap<>();
        map.put("order_id", orderId);

        //執行查詢
        List<Order> orderList = namedParameterJdbcTemplate.query(sql, map, new OrderRowMapper());

        //判斷查詢結果
        if(orderList.size() > 0){
            return orderList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<OrderItem> getOrderItemByOrderId(Integer orderId) {
        //定義 SQL 語句：使用 LEFT JOIN 將 order_item (oi) 與 product (p) 串連起來。
        //目的：透過 product_id 取得商品的「名稱」與「圖片網址」，讓訂單資訊更完整。
        String sql = "SELECT oi.order_item_id, oi.order_id, oi.product_id, oi.quantity, oi.amount, p.product_name, p.image_url " +
                "FROM order_item as oi " +
                "LEFT JOIN product as p ON oi.product_id = p.product_id " +
                "WHERE oi.order_id = :order_id";

        //建立參數 Map
        Map<String, Object> map = new HashMap<> () ;
        map.put("order_id", orderId);

        //執行查詢：將結果透過 OrderItemRowMapper 轉換為 List<OrderItem>
        //**這裡的 RowMapper 必須能處理來自不同表格 (p.product_name) 的欄位
        List<OrderItem> orderItemList = namedParameterJdbcTemplate.query(sql, map, new OrderItemRowMapper());

        //回傳該訂單的所有明細清單
        return orderItemList;
    }

    @Override
    public Integer createOrder(Integer userId, Integer totalAmount) {
        //新增sql之指令，`order`避免語法錯誤
        String sql = "INSERT INTO `order`(user_id, total_amount, created_date, last_modified_date) " +
                "VALUES (:user_id, :total_amount, :created_date, :last_modified_date)";

        //建立參數容器，將 Service 層傳來的 userId (誰買的) 與 totalAmount (總共多少錢) 放入
        Map<String, Object> map = new HashMap<>();
        map.put("user_id", userId);
        map.put("total_amount", totalAmount);

        //取得當前系統時間，同時賦予建立與修改時間
        Date now = new Date();
        map.put("created_date", now);
        map.put("last_modified_date", now);

        //宣告 KeyHolder，準備接收由資料庫自動生成的 order_id
        KeyHolder keyHolder = new GeneratedKeyHolder();

        //將參數 Map 轉為 MapSqlParameterSource 並傳入 keyHolder
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

        //抓取新 ID
        int orderId = keyHolder.getKey().intValue();

        return orderId;
    }

    @Override
    public void createOrderItems(Integer orderId, List<OrderItem> orderItemList) {

        //使用 batchUpdate 一次性加入數據，效率更高
        String sql = "INSERT INTO order_item(order_id, product_id, quantity, amount) " +
                "VALUES (:order_id, :product_id, :quantity, :amount)";

        //準備一個「參數陣列」，長度等於訂單明細的數量。
        MapSqlParameterSource [] parameterSources = new MapSqlParameterSource[orderItemList.size()];

        //使用 for 迴圈將每一項商品資料，封裝進對應的參數物件中
        for ( int i = 0; i < orderItemList.size(); i++){
            OrderItem orderItem = orderItemList.get(i);

            //初始化陣列中的每一個元素
            parameterSources[i] = new MapSqlParameterSource();
            //將資料逐一放入，注意 order_id 是從 Service 層傳下來的「同一張訂單編號」
            parameterSources[i].addValue("order_id", orderId);
            parameterSources[i].addValue("product_id", orderItem.getProductId());
            parameterSources[i].addValue("quantity", orderItem.getQuantity());
            parameterSources[i].addValue("amount", orderItem.getAmount());
        }

        //執行批次更新。Spring 會將這一組參數陣列一次性發送給資料庫，大幅減少連線開銷
        namedParameterJdbcTemplate.batchUpdate(sql, parameterSources);
    }

    private String addFilteringSql(String sql, Map<String, Object> map, OrderQueryParams orderQueryParams){
        //檢查參數物件中是否有傳入 userId
        if(orderQueryParams.getUserId() != null){
            sql = sql + " AND user_id = :user_id";
            map.put("user_id", orderQueryParams.getUserId());
        }

        return sql;
    }
}
