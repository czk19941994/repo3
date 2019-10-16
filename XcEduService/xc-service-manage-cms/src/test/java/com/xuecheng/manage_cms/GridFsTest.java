package com.xuecheng.manage_cms;

import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GridFsTest {
    @Autowired
    private GridFsTemplate gridFsTemplate;
    //存文件
    @Test
    public void testGridFsTemplate()throws Exception{
        //流
        File file=new File("G:\\学成在线资料\\freemarker模板文件\\index_banner.ftl");
        InputStream inputStream=new FileInputStream(file);
        ObjectId objectId = gridFsTemplate.store(inputStream, "index_banner.ftl");
        System.out.println(objectId);
    }
}
