package com.neusoft.coursemgr.common;

import java.util.List;

public class PageResult<T> {
    private long total;
    private List<T> list;

    public PageResult(long total, List<T> list) {
        this.total = total;
        this.list = list;
    }

    public long getTotal() {
        return total;
    }

    public List<T> getList() {
        return list;
    }
}
