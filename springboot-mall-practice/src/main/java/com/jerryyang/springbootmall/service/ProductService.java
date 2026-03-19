package com.jerryyang.springbootmall.service;

import com.jerryyang.springbootmall.dto.ProductRequest;
import com.jerryyang.springbootmall.model.Product;

import java.util.List;

public interface ProductService {

    List<Product> getProducts();

    Product getProductById(Integer productId);

    Integer createProduct(ProductRequest productRequest);

    //沒有返回值
    void updateProduct(Integer productId, ProductRequest productRequest);

    void deleteProductById(Integer productId);

}
