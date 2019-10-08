package com.xuecheng.framework.exception;

import com.google.common.collect.ImmutableMap;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice//控制器增强
public class ExceptionCatch {
    //记录日志 slf4j logger
    private static final Logger LOGGER= LoggerFactory.getLogger(ExceptionCatch.class);
    //定义map，配置异常类型所对应的错误代码
    private static ImmutableMap<Object, Object> EXCEPTIONS;
    //构建map的builder对象
    protected static ImmutableMap.Builder<Object, Object> builder=ImmutableMap.builder();
    //补货客户异常
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public ResponseResult customException(CustomException customException){
        //记录日志
        LOGGER.error("catch Exception:{}",customException.getMessage());
        //拿到错误代码
        ResultCode resultCode = customException.getResultCode();
        //返回给用户
        return new ResponseResult(resultCode);
    }
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseResult exception(Exception exception){
        LOGGER.error("catch exception:{}",exception.getMessage());
        if (EXCEPTIONS==null){
          EXCEPTIONS=  builder.build();//exception构建成功
        }
        ResultCode resultCode = (ResultCode) EXCEPTIONS.get(exception.getClass());
        if(resultCode!=null){
            return new ResponseResult(resultCode);
        }else {
            return new ResponseResult(CommonCode.SERVER_ERROR);
        }
        //return  new ResponseResult(CommonCode.SERVER_ERROR);
    }
    static {
        //定义异常类型所对象异常代码
        builder.put(HttpMessageNotReadableException.class,CommonCode.INVALID_PARA);
    }
}
