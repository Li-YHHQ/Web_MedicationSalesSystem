package com.neusoft.coursemgr.mapper;

import com.neusoft.coursemgr.domain.FileRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FileMapper {

    int insert(FileRecord file);

    FileRecord selectMetaById(@Param("id") Long id);

    FileRecord selectDataById(@Param("id") Long id);

    int deleteById(@Param("id") Long id);
}
