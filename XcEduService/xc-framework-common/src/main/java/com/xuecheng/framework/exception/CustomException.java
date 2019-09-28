package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 自定义异常类
 */
@Getter
@Setter
public class CustomException extends RuntimeException {
    //定义错误代码
    ResultCode resultCode;
    public CustomException(ResultCode resultCode){
        this.resultCode=resultCode;
    }
}
