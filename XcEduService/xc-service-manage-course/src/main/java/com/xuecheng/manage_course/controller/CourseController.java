package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CourseControllerApi;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.service.CourseServcie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/course")
public class CourseController implements CourseControllerApi {
    @Autowired
    private CourseServcie courseServcie;

    @GetMapping("/teachplan/list/{courseid}")
    public TeachplanNode findTeachplanList(@PathVariable("courseid") String courseid) {
        TeachplanNode teachplanNode = courseServcie.findTeachplanNode(courseid);
        return teachplanNode;
    }


    @PostMapping("/teachplan/add")
    public ResponseResult addTeachplan(@RequestBody Teachplan teachplan) {
        return courseServcie.addTeachplan(teachplan);
    }
}
