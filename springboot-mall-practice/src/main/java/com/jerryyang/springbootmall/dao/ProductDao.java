package com.jerryyang.springbootmall.dao;

import com.jerryyang.springbootmall.constant.ProductCategory;
import com.jerryyang.springbootmall.dto.ProductRequest;
import com.jerryyang.springbootmall.model.Product;

import java.util.List;

public interface ProductDao {

    List<Product> getProducts(ProductCategory category, String search);

    Product getProductById(Integer productId);

    Integer createProduct(ProductRequest productRequest);

    void updateProduct(Integer productId, ProductRequest productRequest);

    void deleteProductById(Integer productId);
}
