package com.jerryyang.springbootmall.service.impl;

import com.jerryyang.springbootmall.dao.UserDao;
import com.jerryyang.springbootmall.dto.UserLoginRequest;
import com.jerryyang.springbootmall.dto.UserRegisterRequest;
import com.jerryyang.springbootmall.model.User;
import com.jerryyang.springbootmall.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class UserServiceImpl implements UserService {

    private final static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDao userDao;

    @Override
    public User getUserById(Integer userId) {
        return userDao.getUserById(userId);
    }

    @Override
    public Integer register(UserRegisterRequest userRegisterRequest) {
        //創造新帳號前，先檢查此mail有沒有被註冊過
        User user = userDao.getUserByEmail(userRegisterRequest.getEmail());

        //如果有使用者的資料，回傳http狀態碼400 BAD_REQUEST 表示前端請求參數有問題
        if(user != null){
            log.warn("該email {} 已經被註冊", userRegisterRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        //創建帳號
        return userDao.createUser(userRegisterRequest);
    }

    @Override
    public User login(UserLoginRequest userLoginRequest) {
        //根據前端傳入的 Email，向 DAO 層請求查詢該使用者資料
        User user = userDao.getUserByEmail(userLoginRequest.getEmail());

        //檢查資料庫是否存在該 Email 的紀錄
        if(user == null){
            //若找不到，記錄 Warn Log 並拋出 400 Bad Request 異常
            log.warn("該 email {} 尚未註冊", userLoginRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        //比對資料庫內存儲的密碼與前端傳入的密碼是否一致
        if(user.getPassword().equals(userLoginRequest.getPassword())){
            return user;
        } else{
            log.warn("email {} 的密碼不正確", userLoginRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
}
