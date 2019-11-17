package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.dao.CourseBaseRepository;
import com.xuecheng.manage_course.dao.TeachplanMapper;
import com.xuecheng.manage_course.dao.TeachplanRepository;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CourseServcie {
    //课程计划查询
    @Autowired
    private TeachplanMapper teachplanMapper;
    @Autowired
    private TeachplanRepository teachplanRepository;
    @Autowired
    private CourseBaseRepository courseBaseRepository;
    public TeachplanNode findTeachplanNode(String courseid){
        TeachplanNode teachplanNode = teachplanMapper.selectList(courseid);
        TeachplanNode teachplanNode1 = teachplanNode;
        return teachplanNode1;
    }

    /**
     * 添加课程计划
     * @param teachplan
     * @return
     */
    @Transactional
    public ResponseResult addTeachplan(Teachplan teachplan){
        if (teachplan==null|| StringUtils.isEmpty(teachplan.getCourseid())||StringUtils.isEmpty(teachplan.getPname())){
            ExceptionCast.cast(CommonCode.INVALID_PARA);
        }
        String courseid = teachplan.getCourseid();
        String parentid = teachplan.getParentid();
        if (parentid==null){
            parentid = getTeachplanList(courseid);
        }
        //得到父节点
        Optional<Teachplan> byId = teachplanRepository.findById(parentid);
        String grade = byId.get().getGrade();
        //新节点
        Teachplan teachplan1=new Teachplan();
        //页面提交信息拷贝
        BeanUtils.copyProperties(teachplan,teachplan1);
        teachplan1.setParentid(parentid);
        teachplan1.setCourseid(courseid);
        if (grade.equals("1")){
            teachplan1.setGrade("2");
        }else {
            teachplan1.setGrade("3");
        }
        teachplanRepository.save(teachplan1);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 得到根节点id
     * @param courseid
     * @return
     */
    private String getTeachplanList(String courseid){
        Optional<CourseBase> byId = courseBaseRepository.findById(courseid);
        if (!byId.isPresent()){
            return null;
        }
        CourseBase courseBase = byId.get();
        List<Teachplan> byCourseidAndParentid = teachplanRepository.findByCourseidAndParentid(courseid, "0");
        if (byCourseidAndParentid==null||byCourseidAndParentid.size()==0){
            //查询不到，自动添加根节点
            Teachplan t=new Teachplan();
            t.setParentid("0");
            t.setCourseid(courseid);
            t.setGrade("1");
            t.setStatus("0");
            t.setPname(courseBase.getName());
            teachplanRepository.save(t);
            return t.getId();
        }
        return byCourseidAndParentid.get(0).getId();
    }
}
