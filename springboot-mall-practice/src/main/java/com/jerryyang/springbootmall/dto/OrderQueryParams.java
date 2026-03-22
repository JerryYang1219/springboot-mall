package com.jerryyang.springbootmall.dto;

/*
 * OrderQueryParams 用於封裝訂單查詢的所有篩選與分頁參數。
 * 目的：避免 Service 與 DAO 層的方法參數過於冗長，並提高未來擴充查詢條件時的彈性。
 */

public class OrderQueryParams {

    private Integer userId;
    private Integer limit;
    private Integer offset;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }
}
