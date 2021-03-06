package com.xuecheng.learning.config;

import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 *
 */
@FeignClient(value = XcServiceList.XC_SERVICE_SEARCH)
public interface CourseSearchClient {
    //根据课程计划id获得
    @GetMapping("/search/course/getMedia/{teachplanId}")
    TeachplanMediaPub getMedia(@PathVariable("teachplanId") String teachplanId);
}
