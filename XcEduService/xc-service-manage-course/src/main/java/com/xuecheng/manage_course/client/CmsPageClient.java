package com.xuecheng.manage_course.client;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "XC-SERVICE-MANAGE-CMS") //指定服务名，feign找eurekaa要服务名
public interface CmsPageClient {
    //根据页面id查询页面信息，远程调用cms请求数据
    @GetMapping("/cms/page/get/{id}")
    public CmsPage findCmsPageById(String id);
    //由feign生成代理对象
    //添加页面，用于页面课程预览
    @PostMapping("/cms/page/save")
    CmsPageResult saveCmsPage(@RequestBody CmsPage cmsPage);
    //一键发布
    @PostMapping("/cms/page/postPageQuick")
    CmsPostPageResult postPageQuick(@RequestBody CmsPage cmsPage);
}
