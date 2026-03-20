package com.jerryyang.springbootmall.controller;

import com.jerryyang.springbootmall.dto.UserLoginRequest;
import com.jerryyang.springbootmall.dto.UserRegisterRequest;
import com.jerryyang.springbootmall.model.User;
import com.jerryyang.springbootmall.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    //註冊新帳號
    @PostMapping("/users/register")
    public ResponseEntity<User> register(@RequestBody @Valid UserRegisterRequest userRegisterRequest){
        //新創帳號並得到資料庫所生成的userId
        Integer userId = userService.register(userRegisterRequest);

        //根據userId 查詢這名使用者
        User user = userService.getUserById(userId);

        //將使用者資料回傳給前端
        return ResponseEntity.status(HttpStatus.CREATED).body(user);

    }

    //登入帳號
    @PostMapping("/users/login")
    public ResponseEntity<User> login(@RequestBody @Valid UserLoginRequest userLoginRequest){
        //呼叫 Service 層的 login 方法
        User user = userService.login(userLoginRequest);

        //回傳 HTTP 200 OK，並將登入成功的使用者資訊放在 Response Body 中回傳給前端
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }
}
