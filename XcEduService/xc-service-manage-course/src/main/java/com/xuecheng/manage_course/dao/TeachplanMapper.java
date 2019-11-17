package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import org.apache.ibatis.annotations.Mapper;

/**
 * 查询课程
 */
@Mapper
public interface TeachplanMapper {
    TeachplanNode selectList(String courseId);
}
