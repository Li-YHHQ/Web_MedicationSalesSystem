package com.neusoft.coursemgr.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdateCategoryRequest {

    @NotBlank(message = "分类名称不能为空")
    private String name;

    @NotNull(message = "排序不能为空")
    private Integer sortOrder;

    @NotNull(message = "状态不能为空")
    private Integer status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
