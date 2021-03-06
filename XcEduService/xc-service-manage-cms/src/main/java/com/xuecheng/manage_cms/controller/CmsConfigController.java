package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsConfigControllerApi;
import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.manage_cms.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cms/config")
public class CmsConfigController implements CmsConfigControllerApi {
    @Autowired
    private PageService pageService;
    @Override
    @GetMapping("/getmodel/{id}")
    @ResponseBody
    public CmsConfig getModel(@PathVariable("id") String id) {
        return pageService.getCmsconfigById(id);
    }
}
