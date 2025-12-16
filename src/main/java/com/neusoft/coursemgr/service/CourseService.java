package com.neusoft.coursemgr.service;

import com.neusoft.coursemgr.domain.Course;

import java.util.List;

public interface CourseService {
    Course getById(Long id);
    List<Course> getByTeacher(String teacher);
    List<Course> searchByName(String keyword);
    List<Course> getByCredit(Integer credit);
    List<Course> getByLocation(String location);
    Long create(Course course);
    Integer updateCredit(Long id, Integer credit);
    String updateLocation(Long id, String location);
    boolean deleteById(Long id);
    List<Course> getByCourseCode(String courseCode);
}
