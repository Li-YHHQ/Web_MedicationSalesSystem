package com.neusoft.coursemgr.mapper;

import com.neusoft.coursemgr.domain.Course;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CourseMapper {
    Course selectById(@Param("id") Long id);

    List<Course> selectByTeacher(@Param("teacher") String teacher);

    List<Course> selectByNameLike(@Param("keyword") String keyword);

    List<Course> selectByCredit(@Param("credit") Integer credit);

    List<Course> selectByLocation(@Param("location") String location);

    int insert(Course course);

    int updateCredit(@Param("id") Long id, @Param("credit") Integer credit);

    int updateLocation(@Param("id") Long id, @Param("location") String location);

    int deleteById(@Param("id") Long id);

    List<Course> selectByCourseCode(@Param("courseCode") String courseCode);
}
