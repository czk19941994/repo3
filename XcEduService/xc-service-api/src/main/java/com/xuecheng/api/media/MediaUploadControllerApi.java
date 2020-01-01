package com.xuecheng.api.media;

import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jdk.management.resource.ResourceRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * 妹子管理接口
 */
@Api(value = "媒资管理接口",description = "媒资管理接口，提供文件上传处理等接口")
public interface MediaUploadControllerApi {
    //文件上传的准备工作 校验文件是否存在
    @ApiOperation("文件上传注册")
    ResponseResult register(String fileMd5,String fileName,Long fileSize,String mimeType,String fileExt);
    @ApiOperation("校验分块是否存在")
    CheckChunkResult checkChunk(String fileMd5,Integer chunk,Integer chunkSize);
    @ApiOperation("上传分块")
    ResponseResult uploadChunk(MultipartFile file, String fileMd5, Integer chunk);
    @ApiOperation("合并分块")
    ResponseResult mergeChunk(String fileMd5,String fileName,Long fileSize,String mimeType,String fileExt);
}
