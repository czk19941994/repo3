package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.TeachplanMedia;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
@Api(value = "课程管理接口",description = "cms页面接口的管理，提供课程的增删改查")
public interface CourseControllerApi {
    @ApiOperation("课程计划的查询")
    public TeachplanNode findTeachplanList(String coorseId);
    @ApiOperation("添加课程计划")
    ResponseResult addTeachplan(Teachplan teachplan);
    @ApiOperation("添加课程图片")
    ResponseResult addCoursePic(String courseId,String pic);
    @ApiOperation("查询课程图片")
    public CoursePic findCoursePic(String courseId);
    @ApiOperation("删除课程图片")
    ResponseResult deleteCoursePic(String oourseId);
    @ApiOperation("课程视图的查询")
    public CourseView courseVie(String id);
    @ApiOperation("预览课程")
    CoursePublishResult preview(String id);
    @ApiOperation("课程发布")
    CoursePublishResult publish(String courseId);
    @ApiOperation("保存课程计划与媒资文件关联")
    ResponseResult saveMedia(TeachplanMedia teachplanMedia);
}
