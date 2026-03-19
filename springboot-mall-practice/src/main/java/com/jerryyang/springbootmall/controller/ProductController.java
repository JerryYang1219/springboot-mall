package com.jerryyang.springbootmall.controller;

import com.jerryyang.springbootmall.constant.ProductCategory;
import com.jerryyang.springbootmall.dto.ProductQueryParams;
import com.jerryyang.springbootmall.dto.ProductRequest;
import com.jerryyang.springbootmall.model.Product;
import com.jerryyang.springbootmall.service.ProductService;
import com.jerryyang.springbootmall.util.Page;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated //有@Max、@Min的註解，需加上才會生效
@RestController //Controller的bean
public class ProductController {

    @Autowired
    private ProductService productService;

    //搜尋列表功能
    @GetMapping("/products")
    public ResponseEntity<Page<Product>> getProducts(
            //@RequestParam 從url中取得道的請求參數，required = false不是必填參數
            //查詢條件 Filtering
            @RequestParam(required = false) ProductCategory category, //商品分類
            @RequestParam(required = false) String search,  //關鍵字實作

            //排序 Sorting
            @RequestParam(defaultValue = "created_date") String orderBy, // 設定預設值
            @RequestParam(defaultValue = "desc") String sort, //設定預設值為大->小之排序

            // 分頁 Pagination
            @RequestParam(defaultValue = "5") @Max(1000) @Min(0) Integer limit, //預設取出5筆、限制0-1000筆
            @RequestParam(defaultValue = "0") @Min(0) Integer offset //預設不跳過任何1筆數據
    ){
        //將參數統整至ProductQueryParams做管理
        ProductQueryParams productQueryParams = new ProductQueryParams();
        productQueryParams.setCategory(category);
        productQueryParams.setSearch(search);
        productQueryParams.setOrderBy(orderBy);
        productQueryParams.setSort(sort);
        productQueryParams.setLimit(limit);
        productQueryParams.setOffset(offset);

        //取得 product list 商品列表的數據
        List<Product> productList = productService.getProducts(productQueryParams);

        //取得 product 總數
        Integer total = productService.countProduct(productQueryParams);

        //分頁，返回更多資訊給前端
        Page<Product> page = new Page<>();
        page.setLimit(limit);
        page.setOffset(offset);
        page.setTotal(total);
        page.setResults(productList);

        return ResponseEntity.status(HttpStatus.OK).body(page);
    }

    //查詢商品功能
    @GetMapping("/products/{productId}")
    public ResponseEntity<Product> getProduct(@PathVariable Integer productId){
        Product product = productService.getProductById(productId);

        if(product != null){
            return ResponseEntity.status(HttpStatus.OK).body(product);
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    //新增商品功能
    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody @Valid ProductRequest productRequest){
        Integer productId = productService.createProduct(productRequest);

        Product product = productService.getProductById(productId);

        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Integer productId,
                                                 @RequestBody @Valid ProductRequest productRequest){//接住前端傳過來的參數
                                                // 前端只能去修改ProductRequest中變數的值
        //先查詢要更新的商品是否存在
        Product product = productService.getProductById(productId);

        //回傳給前端說商品不存在
        if(product == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        //productId要更新的是哪一個商品，ProductRequest表示這個商品修改過後的值是什麼
        productService.updateProduct(productId, productRequest);

        //取得更新後的商品出來
        Product updatedProduct = productService.getProductById(productId);

        //商品修改成功、且商品修改過後的值為多少
        return ResponseEntity.status(HttpStatus.OK).body(updatedProduct);
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer productId){
        productService.deleteProductById(productId);

        // http status code 是 204 No Content 表示這個數據已經被刪掉了
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
