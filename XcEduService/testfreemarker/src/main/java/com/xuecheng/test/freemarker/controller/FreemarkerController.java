package com.xuecheng.test.freemarker.controller;

import com.xuecheng.test.freemarker.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/freemarker")
@Controller//不要使用restcontroller
public class FreemarkerController {
    @Autowired
    private RestTemplate restTemplate;
    @RequestMapping("/banner")
    public String index_banner(Map<String,Object> map){
        //请求轮播图
        ResponseEntity<Map> forEntity = restTemplate.getForEntity("http://localhost:31001/cms/config/getmodel/5a791725dd573c3574ee333f", Map.class);
        Map body = forEntity.getBody();
        map.putAll(body);
        return "index_banner";
    }

    //测试一
    @RequestMapping("/test1")
    public String test1(Map<String,Object> map){//以，map为形参，在request域中能够取到
        map.put("name","阿坤");
        //f返回frrmarker的模板位置，基于resource/templates
        Student stu1=new Student();
        stu1.setName("陈彰坤");
        stu1.setAge(20);
        stu1.setMoney(99999.99f);
        //
        Student stu2=new Student();
        stu2.setName("吴丹");
        stu2.setAge(19);
        stu2.setMoney(1777.1f);
        List<Student> list=new ArrayList<>();
        list.add(stu1);
        map.put("list",list);
        //遍历map
        Map<String,Student> stuMap=new HashMap<>();
        stuMap.put("stu1",stu1);
        stuMap.put("stu2",stu2);
        return "test1";
    }
}
