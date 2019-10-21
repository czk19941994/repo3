package com.xuecheng.manage_cms.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsconfigRepository;
import com.xuecheng.manage_cms.dao.CmstemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * @author Administrator
 * @version 1.0
 * @create 2018-09-12 18:32
 **/
@Service
public class PageService {

    @Autowired
    CmsPageRepository cmsPageRepository;
    @Autowired
    private CmsconfigRepository cmsconfigRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private CmstemplateRepository cmstemplateRepository;
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private GridFSBucket gridFSBucket;
    /**
     * 页面查询方法
     * @param page 页码，从1开始记数
     * @param size 每页记录数
     * @param queryPageRequest 查询条件
     * @return
     */
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest){
        //自定义条件查询
        ExampleMatcher exampleMatcher = ExampleMatcher.matching();
        exampleMatcher=exampleMatcher.withMatcher("pageAliase",ExampleMatcher.GenericPropertyMatchers.contains());
        if(queryPageRequest==null){
            QueryPageRequest queryPageRequest1=new QueryPageRequest();
        }
        CmsPage cmsPage=new CmsPage();
        //设置查询参数
        if(StringUtils.isNotBlank(queryPageRequest.getTemplateId())){
            cmsPage.setPageId(queryPageRequest.getTemplateId());
        }
        if(StringUtils.isNotBlank(queryPageRequest.getSiteId())){
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }
        if(StringUtils.isNotBlank(queryPageRequest.getPageAliase())){
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }
        //
        //分页参数
        if(page <=0){
            page = 1;
        }
        page = page -1;
        if(size<=0){
            size = 10;
        }
        //分页查询对象
        Pageable pageable = PageRequest.of(page,size);
        //example查询对象
        Example<CmsPage> example=Example.of(cmsPage,exampleMatcher);
        //执行查询
        Page<CmsPage> all = cmsPageRepository.findAll(example,pageable);
        QueryResult queryResult = new QueryResult();
        queryResult.setList(all.getContent());//数据列表
        queryResult.setTotal(all.getTotalElements());//数据总记录数
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }
    //新增页面
    public CmsPageResult add(CmsPage cmsPage){
        //校验页面是否唯一，页面的index表示唯一性
        CmsPage cmsPagel = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        //页面已经存在，排除异常
        if(cmsPagel!=null){
            //抛出异常
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }

        if(cmsPagel==null){
            //
            cmsPage.setPageId(null);
            cmsPageRepository.save(cmsPage);
            return new CmsPageResult(CommonCode.SUCCESS,cmsPage);
        }
        //添加失败
        return new CmsPageResult(CommonCode.FAIL,null);
    }
    //根据id查询页面
    public CmsPage getById(String id){
        Optional<CmsPage> cmsPages = cmsPageRepository.findById(id);
        if(cmsPages.isPresent()){
            CmsPage cmsPage = cmsPages.get();
            return cmsPage;
        }
        return null;

    }
    //修改页面
    public CmsPageResult edit(String id,CmsPage cmsPage){
        CmsPage cmsPage1 = this.getById(id);
        if(cmsPage1!=null){
            cmsPage1.setTemplateId(cmsPage.getTemplateId());
            cmsPage1.setSiteId(cmsPage.getSiteId());
            cmsPage1.setPageAliase(cmsPage.getPageAliase());
            cmsPage1.setPageName(cmsPage.getPageName());
            cmsPage1.setPageWebPath(cmsPage.getPageWebPath());
            cmsPage1.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            //dataurl
            cmsPage1.setDataUrl(cmsPage.getDataUrl());
            cmsPageRepository.save(cmsPage1);
            return new CmsPageResult(CommonCode.SUCCESS,cmsPage1);
        }
        return new CmsPageResult(CommonCode.FAIL,null);
    }
    //根据id删除页面
    public ResponseResult delete(String id){
        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        if(optional.isPresent()){
            cmsPageRepository.delete(optional.get());
            return ResponseResult.SUCCESS();
        }
        return  ResponseResult.FAIL();
    }
    //根据id查询cmsConfig
    public CmsConfig getCmsconfigById(String id){
        Optional<CmsConfig> cmsConfig = cmsconfigRepository.findById(id);
        if (cmsConfig.isPresent()){
            CmsConfig cmsConfig1 = cmsConfig.get();
            return cmsConfig1;
        }
        return null;
    }
    //页面静态化方法
    public String getPageHtml(String pageId){
        //获得模型数据
        Map model = this.getModelByPageId(pageId);
        if (model==null){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }
        String template = this.getTemplateBypageId(pageId);
        if (StringUtils.isEmpty(template)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //执行静态化
        try {
            final String content = this.generateHtml(template, model);
            return content;
        }catch (Exception e){
            e.printStackTrace();
        }

        return  null;
    }
    //获取模型数据
    private Map getModelByPageId(String pageId){
        //取出页面信息
        CmsPage cmsPage = this.getById(pageId);
        if (cmsPage==null){
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        //取出dataurl
        String dataUrl = cmsPage.getDataUrl();
        if (StringUtils.isBlank(dataUrl)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        Map body = forEntity.getBody();
        return body;
    }
    //获得页面模板信息
    private String getTemplateBypageId(String pageId){
        //取出页面信息
        CmsPage cmsPage = this.getById(pageId);
        if(cmsPage==null){
            //页面不存在
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        String templateId = cmsPage.getTemplateId();
        if (templateId==null){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //获取模板信息
        Optional<CmsTemplate> cmsTemplate = cmstemplateRepository.findById(templateId);
        if (cmsTemplate.isPresent()){
            CmsTemplate cmsTemplate1 = cmsTemplate.get();
            //获得模板文件id
            String templateFileId = cmsTemplate1.getTemplateFileId();
            //从GridFs中获得内容
            //根据文件id获得文件
            GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
            //打开下载流对象
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
            //u获得GridFsrESOURCE对象
            GridFsResource gridFsResource=new GridFsResource(gridFSFile,gridFSDownloadStream);
            try{
                String content = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
                return content;
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return null;
    }
    //执行静态化
    public String generateHtml(String template,Map model) throws IOException, TemplateException {
        //创建配置对象
        Configuration configuration=new Configuration(Configuration.getVersion());
        //创建模板加载器
        StringTemplateLoader stringTemplateLoader=new StringTemplateLoader();
        stringTemplateLoader.putTemplate("template",template);
        //配置模板加载器
        configuration.setTemplateLoader(stringTemplateLoader);
        //获取模板
        Template configurationTemplate = configuration.getTemplate("template");
        //api静态化
        String content = FreeMarkerTemplateUtils.processTemplateIntoString(configurationTemplate, model);
        return content;
    }
}
