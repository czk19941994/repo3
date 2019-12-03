package com.xuecheng.framework.domain.course.response;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class CoursePublishResult extends ResponseResult {
    private String previeUrl; //页面预览url,必须得到页面的id才可以瓶装
    public CoursePublishResult(ResultCode resultCode,String previeUrl){
        super(resultCode);
        this.previeUrl=previeUrl;
    }
}
