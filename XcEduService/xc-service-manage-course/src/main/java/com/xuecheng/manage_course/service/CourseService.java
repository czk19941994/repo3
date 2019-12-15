package com.xuecheng.manage_course.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.dao.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Administrator
 * @version 1.0
 **/
@Service
public class CourseService {
    @Autowired
    private CoursePicRepository coursePicRepository;
    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    TeachplanRepository teachplanRepository;

    @Autowired
    CourseBaseRepository courseBaseRepository;
    @Autowired
    private CourseMarketRepository courseMarketRepository;
    @Autowired
    private CoursePubRepository coursePubRepository;
    @Autowired
    private CmsPageClient cmsPageClient;
    @Value("${course‐publish.dataUrlPre}")
    private String publish_dataUrlPre;
    @Value("${course‐publish.pagePhysicalPath}")
    private String publish_page_physicalpath;
    @Value("${course‐publish.pageWebPath}")
    private String publish_page_webpath;
    @Value("${course‐publish.siteId}")
    private String publish_siteId;
    @Value("${course‐publish.templateId}")
    private String publish_templateId;
    @Value("${course‐publish.previewUrl}")
    private String previewUrl;
    //查询课程计划
    public TeachplanNode findTeachplanList(String courseId){
        return teachplanMapper.selectList(courseId);
    }

    @Transactional
    public ResponseResult addTeachplan(Teachplan teachplan) {

        if(teachplan == null ||
                StringUtils.isEmpty(teachplan.getPname()) ||
                StringUtils.isEmpty(teachplan.getCourseid())){
            ExceptionCast.cast(CommonCode.INVALID_PARA);
        }
        //课程id
        String courseid = teachplan.getCourseid();
        //父结点的id
        String parentid = teachplan.getParentid();
        if(StringUtils.isEmpty(parentid)){
            //获取课程的根结点
            parentid = getTeachplanRoot(courseid);
        }
        //查询根结点信息
        Optional<Teachplan> optional = teachplanRepository.findById(parentid);
        Teachplan teachplan1 = optional.get();
        //父结点的级别
        String parent_grade = teachplan1.getGrade();
        //创建一个新结点准备添加
        Teachplan teachplanNew = new Teachplan();
        //将teachplan的属性拷贝到teachplanNew中
        BeanUtils.copyProperties(teachplan,teachplanNew);
        //要设置必要的属性
        teachplanNew.setParentid(parentid);
        if(parent_grade.equals("1")){
            teachplanNew.setGrade("2");
        }else{
            teachplanNew.setGrade("3");
        }
        teachplanNew.setStatus("0");//未发布
        teachplanRepository.save(teachplanNew);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //获取课程的根结点
    public String getTeachplanRoot(String courseId){
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if(!optional.isPresent()){
            return null;
        }
        CourseBase courseBase = optional.get();
        //调用dao查询teachplan表得到该课程的根结点（一级结点）
        List<Teachplan> teachplanList = teachplanRepository.findByCourseidAndParentid(courseId, "0");
        if(teachplanList == null || teachplanList.size()<=0){
            //新添加一个课程的根结点
            Teachplan teachplan = new Teachplan();
            teachplan.setCourseid(courseId);
            teachplan.setParentid("0");
            teachplan.setGrade("1");//一级结点
            teachplan.setStatus("0");
            teachplan.setPname(courseBase.getName());
            teachplanRepository.save(teachplan);
            return teachplan.getId();

        }
        //返回根结点的id
        return teachplanList.get(0).getId();

    }
    //想课程管理数据库添加课程图片管理信息
    @Transactional
    public ResponseResult addCoursePic(String courseId,String pic){
        CoursePic coursePic=null;
        Optional<CoursePic> byId = coursePicRepository.findById(courseId);
        if (byId.isPresent()){
            coursePic=byId.get();
        }
        if (coursePic==null){
             coursePic=new CoursePic();
        }
        coursePic.setPic(pic);
        coursePic.setCourseid(courseId);
        coursePicRepository.save(coursePic);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    public CoursePic findCoursePic(String courseId) {
        Optional<CoursePic> byId = coursePicRepository.findById(courseId);
        if (byId.isPresent()){
            return byId.get();
        }
        return null;
    }
    @Transactional
    public ResponseResult deleteCousePic(String courseId) {
        long l = coursePicRepository.deleteByCourseid(courseId);
        if (l>0){
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }

    /**
     * 查询课程视图
     * @param id
     * @return
     */
    public CourseView getCourseView(String id) {
        CourseView courseView=new CourseView();
        //课程基本信息
        Optional<CourseBase> optional = courseBaseRepository.findById(id);
        if (optional.isPresent()){
            CourseBase courseBase = optional.get();
            courseView.setCourseBase(courseBase);
        }
        //查询课程图片
        Optional<CoursePic> coursePicOptional = coursePicRepository.findById(id);
        if (coursePicOptional.isPresent()){
            CoursePic coursePic = coursePicOptional.get();
            courseView.setCoursePic(coursePic);
        }
        //查询课程营销信息
        Optional<CourseMarket> courseMarketOptional = courseMarketRepository.findById(id);
        if (courseMarketOptional.isPresent()){
            CourseMarket courseMarket = courseMarketOptional.get();
            courseView.setCourseMarket(courseMarket);
        }
        //课程计划信息
        TeachplanNode teachplanNode = teachplanMapper.selectList(id);
        courseView.setTeachplanNode(teachplanNode);
        return courseView;
    }
    //课程预览
    public CoursePublishResult preview(String courseId) {
        //CourseBase one = this.findCourseBaseById(courseId);
        Optional<CourseBase> byId = courseBaseRepository.findById(courseId);
        CourseBase one=null;
        if (byId.isPresent()){
            one=byId.get();
        }
        //发布课程预览页面
        CmsPage cmsPage = new CmsPage();
        //站点
        cmsPage.setSiteId(publish_siteId);
        //课程预览站点
        // 模板
        cmsPage.setTemplateId(publish_templateId);
        //页面名称
        cmsPage.setPageName(courseId+".html");
        //页面别名
        cmsPage.setPageAliase(one.getName());
        //页面访问路径
        cmsPage.setPageWebPath(publish_page_webpath);
        //页面存储路径
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);
        //数据url
        cmsPage.setDataUrl(publish_dataUrlPre+courseId);
        //远程请求cms保存页面信息
        CmsPageResult cmsPageResult = cmsPageClient.saveCmsPage(cmsPage);
        if (!cmsPageResult.isSuccess()){
            return new CoursePublishResult(CommonCode.FAIL,null);
        }
        String pageId = cmsPageResult.getCmsPage().getPageId();
        String pageUrl=previewUrl+pageId;
        return new CoursePublishResult(CommonCode.SUCCESS,pageUrl);
        //请求cms添加页面
        //瓶装页面预览url
        //返回coursepublishresult
    }
    //课程发布
    @Transactional
    public CoursePublishResult publish(String courseId) {
        CmsPage cmsPage=new CmsPage();
        Optional<CourseBase> byId = courseBaseRepository.findById(courseId);
        CourseBase one=null;
        if (byId.isPresent()){
            one=byId.get();
        }
        //站点
        cmsPage.setSiteId(publish_siteId);
        //课程预览站点
        // 模板
        cmsPage.setTemplateId(publish_templateId);
        //页面名称
        cmsPage.setPageName(courseId+".html");
        //页面别名
        cmsPage.setPageAliase(one.getName());
        //页面访问路径
        cmsPage.setPageWebPath(publish_page_webpath);
        //页面存储路径
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);
        //数据url
        cmsPage.setDataUrl(publish_dataUrlPre+courseId);
        CmsPostPageResult cmsPostPageResult = cmsPageClient.postPageQuick(cmsPage);
        if (!cmsPostPageResult.isSuccess()){
            return new CoursePublishResult(CommonCode.FAIL,null);
        }
        //课程状态已发布
        CourseBase courseBase = saveCoursePublishState(courseId);
        if (cmsPage==null){
            ExceptionCast.cast(CommonCode.FAIL);
        }
        //保存课程的索引信息
        CoursePub coursePub = createCoursePub(courseId);
        //保存到数据库
        CoursePub coursePub1 = saveCoursePub(courseId, coursePub);
        //缓存课程信息
        String pageUrl = cmsPostPageResult.getPageUrl();
        return new CoursePublishResult(CommonCode.SUCCESS,pageUrl);
    }
    public CourseBase saveCoursePublishState(String courseId){
        Optional<CourseBase> byId = courseBaseRepository.findById(courseId);
        if (!byId.isPresent()){
            ExceptionCast.cast(CommonCode.FAIL);
        }
        CourseBase courseBase = byId.get();
        courseBase.setStatus("202002");
        courseBaseRepository.save(courseBase);
        return courseBase;
    }
    //创建coursepub对象
    public CoursePub createCoursePub(String id){
        CoursePub coursePub=new CoursePub();
        //根据课程id查询course_base
        Optional<CourseBase> byId = courseBaseRepository.findById(id);
        if (byId.isPresent()){
            CourseBase courseBase = byId.get();
            //将coursebase拷贝到coursepub
            BeanUtils.copyProperties(courseBase,coursePub);
        }
        //查询课程图片
        Optional<CoursePic> picOptional = coursePicRepository.findById(id);
        if(picOptional.isPresent()){
            CoursePic coursePic = picOptional.get();
            BeanUtils.copyProperties(coursePic, coursePub);
        }
        //课程营销信息
        Optional<CourseMarket> marketOptional = courseMarketRepository.findById(id);
        if(marketOptional.isPresent()){
            CourseMarket courseMarket = marketOptional.get();
            BeanUtils.copyProperties(courseMarket, coursePub);
        }
        //课程计划
        TeachplanNode teachplanNode = teachplanMapper.selectList(id);
        //将课程计划转成json
        String teachplanString = JSON.toJSONString(teachplanNode);
        coursePub.setTeachplan(teachplanString);
        return coursePub;
    }
    //保存coursepub
    public CoursePub saveCoursePub(String id,CoursePub coursePub){
        //根据课程id查询coursePub
        Optional<CoursePub> byId = coursePubRepository.findById(id);
        CoursePub coursePub1=null;
        if (byId.isPresent()){
            coursePub1=byId.get();
        }else {
            coursePub1=new CoursePub();
        }
        //拷贝
        BeanUtils.copyProperties(coursePub,coursePub1);
        coursePub1.setId(id);
        //时间戳
        coursePub1.setTimestamp(new Date());
        //发布时间
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = simpleDateFormat.format(new Date());
        coursePub1.setPubTime(format);
        coursePubRepository.save(coursePub1);
        return coursePub1;
    }
}
