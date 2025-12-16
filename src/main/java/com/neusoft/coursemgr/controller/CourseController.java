package com.neusoft.coursemgr.controller;

import com.neusoft.coursemgr.common.ApiResponse;
import com.neusoft.coursemgr.domain.*;
import com.neusoft.coursemgr.service.CourseService;

import java.util.List;

public class CourseController {

    private CourseService courseService;

    // 41 根据课程ID查询
    public ApiResponse<Course> getById(Long id) {
        return ApiResponse.ok(courseService.getById(id));
    }

    // 42 根据授课教师查询
    public ApiResponse<List<Course>> getByTeacher(String name) {
        return ApiResponse.ok(courseService.getByTeacher(name));
    }

    // 43 课程名模糊查询
    public ApiResponse<List<Course>> search(String keyword) {
        return ApiResponse.ok(courseService.searchByName(keyword));
    }

    // 44 按学分
    public ApiResponse<List<Course>> byCredit(Integer credit) {
        return ApiResponse.ok(courseService.getByCredit(credit));
    }

    // 45 按地点
    public ApiResponse<List<Course>> byLocation(String location) {
        return ApiResponse.ok(courseService.getByLocation(location));
    }

    // 46 新增课程
    public ApiResponse<Long> create(CreateCourseRequest req) {
        Course c = new Course();
        c.setCourseName(req.getCourseName());
        c.setCredit(req.getCredit());
        c.setTeacher(req.getTeacher());
        c.setLocation(req.getLocation());
        c.setCourseCode(req.getCourseCode());
        return ApiResponse.ok("created", courseService.create(c));
    }

    // 47 修改学分
    public ApiResponse<Integer> updateCredit(Long id, UpdateCreditRequest req) {
        return ApiResponse.ok(courseService.updateCredit(id, req.getCredit()));
    }

    // 48 修改地点
    public ApiResponse<String> updateLocation(Long id, UpdateLocationRequest req) {
        return ApiResponse.ok(courseService.updateLocation(id, req.getLocation()));
    }

    // 49 删除
    public ApiResponse<String> delete(Long id) {
        boolean ok = courseService.deleteById(id);
        return ok ? ApiResponse.ok("删除成功", "success") : ApiResponse.fail("未找到记录");
    }

    // 50 课程编码
    public ApiResponse<List<Course>> byCode(String courseCode) {
        return ApiResponse.ok(courseService.getByCourseCode(courseCode));
    }
}
