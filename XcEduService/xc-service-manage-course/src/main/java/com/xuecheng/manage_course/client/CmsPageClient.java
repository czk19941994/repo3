package com.xuecheng.manage_course.client;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
@FeignClient(value = "XC-SERVICE-MANAGE-CMS") //指定服务名，feign找eurekaa要服务名
public interface CmsPageClient {
    //根据页面id查询页面信息，远程调用cms请求数据
    @GetMapping("/cms/page/get/{id}")
    public CmsPage findCmsPageById(String id);
    //由feign生成代理对象
}
