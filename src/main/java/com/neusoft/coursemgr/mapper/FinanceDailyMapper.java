package com.neusoft.coursemgr.mapper;

import com.neusoft.coursemgr.domain.FinanceDaily;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface FinanceDailyMapper {

    int insert(FinanceDaily financeDaily);

    int updateByDate(FinanceDaily financeDaily);

    FinanceDaily selectByDate(@Param("date") LocalDate date);

    List<FinanceDaily> selectList(@Param("startDate") String startDate,
                                  @Param("endDate") String endDate);
}
