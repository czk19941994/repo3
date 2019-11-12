package com.xuecheng.manage_cms_client.service;


import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.manage_cms_client.dao.CmsPageRepository;
import com.xuecheng.manage_cms_client.dao.CmsSiteRepository;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.Optional;

@Service
public class PageService {
    //日志
    private static final Logger LOGGER= LoggerFactory.getLogger(PageService.class);
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Qualifier("GridFSBucketImpl")
    private GridFSBucket gridFSBucket;
    @Autowired
    private CmsPageRepository cmsPageRepository;
    @Autowired
    private CmsSiteRepository cmsSiteRepository;
    //保存hetml页面到物理路径
    public void savePageToServerPath(String pageId){

        //通过cmspage获得htmlfiled内人
        CmsPage cmsPage = findCmsPageById(pageId);
        //获取gtml文件id
        String htmlFileId = cmsPage.getHtmlFileId();
        //获得输入流
      try {
          InputStream inputStream = this.getFileById(htmlFileId);
          if (inputStream==null){
              LOGGER.error("getFileById inputream is null ",htmlFileId);
              return;
          }
          //得到siteid
          String siteId = cmsPage.getSiteId();
          //得到站点的路径
          CmsSite cmsSite = this.findCmsSiteById(siteId);
          String physicalPath = cmsSite.getSitePhysicalPath();
          //得到页面的物理路径
          String sitePath=physicalPath+cmsPage.getPagePhysicalPath()+cmsPage.getPageName();
          //将文件写入磁盘
          FileOutputStream fileOutputStream=new FileOutputStream(new File(sitePath));
          IOUtils.copy(inputStream,fileOutputStream);
          inputStream.close();
          fileOutputStream.close();
      }catch (Exception ex){
          ex.printStackTrace();
      }
    }
    //根据pageid获得页面
    public CmsPage findCmsPageById(String pageId){
        Optional<CmsPage> byId = cmsPageRepository.findById(pageId);
        if (byId.isPresent()){
            return byId.get();
        }
        return null;
    }
    //根据文件id从gridfs中查询文件内容
    public InputStream getFileById(String fileId) throws Exception{
        //文件对象
        GridFSFile gridFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
        //打开下载刘
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFile.getObjectId());
        //定义gridfsresource
        GridFsResource gridFsResource=new GridFsResource(gridFile,gridFSDownloadStream);
        //
        return  gridFsResource.getInputStream();
    }
    //获得站点对象
    public CmsSite findCmsSiteById(String siteId){
        Optional<CmsSite> byId = cmsSiteRepository.findById(siteId);
        if (byId.isPresent()){
            return byId.get();
        }
        return null;
    }
}
