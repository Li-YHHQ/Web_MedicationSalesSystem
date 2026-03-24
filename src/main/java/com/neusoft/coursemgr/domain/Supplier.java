package com.neusoft.coursemgr.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Supplier {
    private Integer id;
    private String name;
    private String contact;
    private String phone;
    private String address;
    private String remark;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
