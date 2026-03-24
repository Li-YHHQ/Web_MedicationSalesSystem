package com.neusoft.coursemgr.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DashboardMapper {

    int countTotalDrugs();

    long sumTotalStock();
}
