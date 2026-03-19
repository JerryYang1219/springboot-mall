package com.jerryyang.springbootmall.dao.impl;

import com.jerryyang.springbootmall.constant.ProductCategory;
import com.jerryyang.springbootmall.dao.ProductDao;
import com.jerryyang.springbootmall.dto.ProductQueryParams;
import com.jerryyang.springbootmall.dto.ProductRequest;
import com.jerryyang.springbootmall.model.Product;
import com.jerryyang.springbootmall.rowmapper.ProductRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ProductDaoImpl implements ProductDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<Product> getProducts(ProductQueryParams productQueryParams) {
        String sql = "SELECT product_id, product_name, category, image_url, price, stock, description, " +
                "created_date, last_modified_date FROM product WHERE 1=1"; // WHERE 1=1 不影響查詢，為了方便連接sql

        Map<String, Object> map = new HashMap<>();

        //在controller中設定category、search為不必填參數，有可能為null，所以需有if判斷式不是null時才拼上sql語法
        //查詢商品分類
        if(productQueryParams.getCategory() != null){
            sql = sql + " AND category = :category"; //AND前面必需要有空白
            map.put("category", productQueryParams.getCategory().name());
        }

        //篩選出包含這個關鍵字的商品
        if(productQueryParams.getCategory() != null){
            sql = sql + " AND product_name LIKE :search";
            map.put("search", "%" + productQueryParams.getCategory() + "%"); //模糊查詢%需要寫在map的值中，寫在sql語法會報錯
        }

        //使用query()查詢商品數據
        List<Product> productList = namedParameterJdbcTemplate.query(sql, map, new ProductRowMapper());

        return productList;
    }

    @Override
    public Product getProductById(Integer productId) {
        String sql = "SELECT product_id, product_name, category, image_url, price, stock, description, " +
                "created_date, last_modified_date FROM product WHERE product_id = :productId";

        Map<String, Object> map = new HashMap<>();
        map.put("productId", productId);

        List<Product> productList = namedParameterJdbcTemplate.query(sql, map, new ProductRowMapper());

        if(productList.size() > 0){
            return productList.get(0);
        }else {
            return null;
        }
    }

    @Override
    public Integer createProduct(ProductRequest productRequest) {
        String sql = "INSERT INTO product(product_name, category, image_url, price, stock, " +
                "description, created_date, last_modified_date) " +
                "VALUES (:product_name, :category, :image_url, :price, :stock, :description, " +
                ":created_date, :last_modified_date)";

        Map<String, Object> map = new HashMap<>();
        map.put("product_name", productRequest.getProductName());
        map.put("category", productRequest.getCategory().toString());
        map.put("image_url", productRequest.getImageUrl());
        map.put("price", productRequest.getPrice());
        map.put("stock", productRequest.getStock());
        map.put("description", productRequest.getDescription());

        Date now = new Date();
        map.put("created_date", now);
        map.put("last_modified_date", now);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

        int productId = keyHolder.getKey().intValue();

        return productId;
    }

    //更新商品功能
    @Override
    public void updateProduct(Integer productId, ProductRequest productRequest) {
        String sql = "UPDATE product SET product_name = :product_name, category = :category, image_url = :image_url, " +
                "price = :price, stock = :stock, description = :description, last_modified_date = :last_modified_date" +
                " WHERE product_id = :product_id";

        // 將 productId 映射至 SQL 具名參數 :product_id
        Map<String, Object> map = new HashMap<>();
        map.put("product_id", productId);

        // 將 ProductRequest 中的資料映射至 SQL 具名參數
        // 將前端傳入的 DTO 欄位逐一填入 Map，Key 值必須與 SQL 裡的 :name 完全一致
        map.put("product_name", productRequest.getProductName());
        map.put("category", productRequest.getCategory().toString());
        map.put("image_url", productRequest.getImageUrl());
        map.put("price", productRequest.getPrice());
        map.put("stock", productRequest.getStock());
        map.put("description", productRequest.getDescription());

        //更新 新的修改時間
        map.put("last_modified_date", new Date());

        // 傳入 SQL 與參數映射表，執行資料庫修改
        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public void deleteProductById(Integer productId) {
        String sql = "DELETE FROM product WHERE product_id = :product_id";

        // 將 productId 映射至 SQL 具名參數 :product_id
        Map<String, Object> map = new HashMap<>();
        map.put("product_id", productId);

        // 傳入 SQL 與參數映射表，執行資料庫的刪除動作
        namedParameterJdbcTemplate.update(sql, map);
    }
}
