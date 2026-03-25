## SpringBoot-Mall-API
這是一個基於 Spring Boot 3 構建的電商後端 RESTful API 系統。完整實作了商品管理、會員認證與訂單處理等核心商務邏輯。

---

## 技術棧 
* **Technical Stack:** Java 17, Spring Boot 3 (RESTful API Design)
* **Database:** MySQL, Spring JDBC
* **Security:** Password Hashing 
* **Architecture:** Layered Architecture (Controller, Service, DAO)

---

## 實作功能 
### 1. 商品功能 (Product Management)
* **進階查詢列表：** 實作了 **過濾 (Filtering)**、**排序 (Sorting)** 與 **分頁 (Pagination)**，支援動態 SQL 查詢。
* **完整 CRUD：** 提供商品的新增、查詢、修改與刪除 API。

### 2. 帳號功能 (User Authentication)
* **註冊與登入：** 實作使用者註冊與登入邏輯。
* **資安防護：** 密碼採用雜湊 (Hashing) 儲存，確保使用者的資訊安全。

### 3. 訂單功能 (Order System)
* **創建訂單：** 整合 `@Transactional` 事務管理。
* **查詢訂單列表：** 實作動態 SQL 篩選與分頁查詢 (Pagination)。

---

## API Examples

---

### 1. User Login

**POST** `/users/login`

#### Request:
```json
{
  "email": "test6@gmail.com",
  "password": "123"
}
```
#### Response:
```json
{
  "userId": 6,
  "createDate": "2026-03-20 21:15:19",
  "lastModifiedDate": "2026-03-20 21:15:19",
  "email": "test6@gmail.com"
}
```
### 2. Create Product
POST /products

#### Request:
```json
{
  "productName": "Toyota",
  "category": "CAR",
  "imageUrl": "https://www.hotaimotor.com.tw/htfile/news-images/news-content-1735812859471.png",
  "price": 1000,
  "stock": 5
}
```
#### Response:
```json
{
  "productId": 3,
  "productName": "Toyota",
  "category": "CAR",
  "imageUrl": "https://www.hotaimotor.com.tw/htfile/news-images/news-content-1735812859471.png",
  "price": 1000,
  "stock": 5,
  "description": null,
  "createdDate": "2026-03-19 14:13:15",
  "lastModifiedDate": "2026-03-19 14:13:15"
}
```

---

## 📂 專案結構 (Directory Structure)
```text
src/main/java/com/jerryyang/springbootmall
├── constant/      # 列舉與常數
├── controller/    # REST API 控制層：處理 HTTP 請求與參數校驗
├── service/       # 業務邏輯層：處理複雜運算與邏輯操作
├── dao/           # 資料存取層：實作動態 SQL 與資料庫互動
├── rowmapper/     # ORM 映射：實作 ResultSet 映射，解耦資料庫與實體物件
├── dto/           # Data Transfer Objects
└── model/         # 資料物件：定義資料表映射與業務擴充物件
