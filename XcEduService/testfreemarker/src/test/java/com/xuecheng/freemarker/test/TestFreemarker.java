package com.xuecheng.freemarker.test;

import com.xuecheng.test.freemarker.model.Student;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SpringBootTest
@RunWith(SpringRunner.class)
@SpringBootConfiguration
public class TestFreemarker {
    @Test
    public void tsetFree() throws IOException, TemplateException {
        //定义配置类
        Configuration configuration=new freemarker.template.Configuration(freemarker.template.Configuration.getVersion());
        //定义模板-获得模板路径
        String path = this.getClass().getResource("/").getPath();
        configuration.setDirectoryForTemplateLoading(new File(path+"/templates/"));
        //获得模板文件内容
        Template template = configuration.getTemplate("test1.ftl");
        Map map = getMap();
        //静态化 返回字符串
        String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
       // System.out.println(content);
        //生成静态文件存放在磁盘
        InputStream inputStream = IOUtils.toInputStream(content);
        FileOutputStream fileOutputStream=new FileOutputStream(new File("g:/test1.html"));
        IOUtils.copy(inputStream,fileOutputStream);
        inputStream.close();
        fileOutputStream.close();
    }
    //获取数据模型
    public Map getMap(){
        Map map=new HashMap();
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
        return map;
    }
}
