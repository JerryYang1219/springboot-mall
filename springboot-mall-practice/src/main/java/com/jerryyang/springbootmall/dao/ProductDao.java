package com.jerryyang.springbootmall.dao;

import com.jerryyang.springbootmall.model.Product;

public interface ProductDao {

    Product getProductById(Integer productId);
}
