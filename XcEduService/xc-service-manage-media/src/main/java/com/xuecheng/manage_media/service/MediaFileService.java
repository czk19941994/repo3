package com.xuecheng.manage_media.service;

import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MediaFileService {
    @Autowired
    private MediaFileRepository mediaFileRepository;

    /**
     * 查询我的媒资列表
     * @param page
     * @param size
     * @param queryMediaFileRequest
     * @return
     */
    public QueryResponseResult<MediaFile> findList(int page, int size, QueryMediaFileRequest queryMediaFileRequest) {
        MediaFile mediaFile=new MediaFile();
        if (queryMediaFileRequest==null){
            queryMediaFileRequest=new QueryMediaFileRequest();
        }
        if (StringUtils.isNotEmpty(queryMediaFileRequest.getFileOriginalName())){
            mediaFile.setFileOriginalName(queryMediaFileRequest.getFileOriginalName());
        }
        if (StringUtils.isNotEmpty(queryMediaFileRequest.getTag())){
            mediaFile.setTag(queryMediaFileRequest.getTag());
        }
        if (StringUtils.isNotEmpty(queryMediaFileRequest.getProcessStatus())){
            mediaFile.setProcessStatus(queryMediaFileRequest.getProcessStatus());
        }
        //条件匹配器 精确匹配是默认的 contain是模糊查询 exact是精确查询
        ExampleMatcher exampleMatcher=ExampleMatcher.matching().withMatcher("tag",ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("fileOriginalName",ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("processStatus",ExampleMatcher.GenericPropertyMatchers.exact());

        //定义example
        Example<MediaFile> example=Example.of(mediaFile,exampleMatcher);
        //分页查询对象
        if (page<=0){
            page=1;
        }
        page=page-1;
        if (size<=0){
            size=10;
        }
        Pageable pageable=new PageRequest(page,size);
        Page<MediaFile> all = mediaFileRepository.findAll(example, pageable);
        long totalElements = all.getTotalElements();
        List<MediaFile> content = all.getContent();
        //返回的数据集
        QueryResult<MediaFile> queryResult=new QueryResult();
        queryResult.setList(content);
        queryResult.setTotal(totalElements);
        return new QueryResponseResult<>(CommonCode.SUCCESS,queryResult);
    }
}
