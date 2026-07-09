package com.example.web.dto;

import java.io.Serializable;

public class AppointOrderMessageDto implements Serializable {

    private static final long serialVersionUID = 1L;  // ✅ 添加序列化版本号

    private String orderId;
    private AppointRecordDto data;
    private Long timestamp;

    // 构造函数
    public AppointOrderMessageDto() {
        this.timestamp = System.currentTimeMillis();
    }

    public AppointOrderMessageDto(String orderId, AppointRecordDto data) {
        this.orderId = orderId;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    // Getter 和 Setter
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public AppointRecordDto getData() {
        return data;
    }

    public void setData(AppointRecordDto data) {
        this.data = data;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}