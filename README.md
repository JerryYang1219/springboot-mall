## SpringBoot-Mall-API
這是一個基於 Spring Boot 3 構建的電商後端系統。本專案實作了從商品功能、帳號功能、訂單功能。

---

## 技術棧 
* **Framework:** Java 17, Spring Boot 3
* **Database:** MySQL, Spring JDBC
* **Security:** Password Hashing 
* **Architecture:** Layered Architecture (Controller, Service, DAO)

---

## 實作功能 

### 1. 商品功能 (Product Management)
* **進階查詢列表：** 實作了 **Filtering (過濾)**、**Sorting (排序)** 與 **Pagination (分頁)**，支援動態 SQL 查詢。
* **完整 CRUD：** 提供商品的新增、查詢、修改與刪除 API。

### 2. 帳號功能 (User Authentication)
* **註冊與登入：** 實作使用者註冊與登入邏輯。
* **資安防護：** 密碼採用雜湊 (Hashing) 儲存，確保使用者的資訊安全。

### 3. 訂單功能 (Order System)
* **創建訂單：** 整合 `@Transactional` 事務管理。
* **查詢訂單列表：**

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
