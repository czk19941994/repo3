package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
@Api(value = "课程管理接口",description = "cms页面接口的管理，提供课程的增删改查")
public interface CourseControllerApi {
    @ApiOperation("课程计划的查询")
    public TeachplanNode findTeachplanList(String coorseId);
    @ApiOperation("添加课程计划")
    ResponseResult addTeachplan(Teachplan teachplan);
}
