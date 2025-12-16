package com.neusoft.coursemgr.service.impl;

import com.neusoft.coursemgr.domain.Course;
import com.neusoft.coursemgr.mapper.CourseMapper;
import com.neusoft.coursemgr.service.CourseService;

import java.util.List;

public class CourseServiceImpl implements CourseService {

    private CourseMapper courseMapper;

    @Override
    public Course getById(Long id) { return courseMapper.selectById(id); }

    @Override
    public List<Course> getByTeacher(String teacher) { return courseMapper.selectByTeacher(teacher); }

    @Override
    public List<Course> searchByName(String keyword) { return courseMapper.selectByNameLike(keyword); }

    @Override
    public List<Course> getByCredit(Integer credit) { return courseMapper.selectByCredit(credit); }

    @Override
    public List<Course> getByLocation(String location) { return courseMapper.selectByLocation(location); }

    @Override
    public Long create(Course course) {
        courseMapper.insert(course);
        return course.getId();
    }

    @Override
    public Integer updateCredit(Long id, Integer credit) {
        courseMapper.updateCredit(id, credit);
        return credit;
    }

    @Override
    public String updateLocation(Long id, String location) {
        courseMapper.updateLocation(id, location);
        return location;
    }

    @Override
    public boolean deleteById(Long id) { return courseMapper.deleteById(id) > 0; }

    @Override
    public List<Course> getByCourseCode(String courseCode) { return courseMapper.selectByCourseCode(courseCode); }
}
