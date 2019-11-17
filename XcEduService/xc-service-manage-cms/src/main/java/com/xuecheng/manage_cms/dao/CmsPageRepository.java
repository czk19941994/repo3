package com.xuecheng.manage_cms.dao;


import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;



public interface CmsPageRepository extends MongoRepository<CmsPage,String> {//制定查询的模型累
    //根据页面名称查询
    CmsPage findByPageName(String pageName);
    //根据别名查询
    CmsPage findByPageAliase(String pageAliase);
    //根据页面id,站点id,页面路径查询
    CmsPage findByPageNameAndSiteIdAndPageWebPath(String pageName,String siteId,String pageWebpath);
}
