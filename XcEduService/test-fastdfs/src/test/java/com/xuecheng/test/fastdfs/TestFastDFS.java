package com.xuecheng.test.fastdfs;

import org.csource.fastdfs.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileOutputStream;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFastDFS {
    @Test
    public void testUpload(){
        try {
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            //定义trackerclient trackserver
            TrackerClient trackerClient=new TrackerClient();
            //链接tracker
            TrackerServer connection = trackerClient.getConnection();
            //获取storage
            StorageServer storeStorage = trackerClient.getStoreStorage(connection);
            //创建storageclient
            StorageClient1 storageClient1=new StorageClient1(connection,storeStorage);
            //上传文件
            String filePath="C:\\Users\\CZK\\Desktop\\timg.jpg";
            String fileId = storageClient1.upload_file1(filePath, "jpg", null);
            System.out.printf(fileId);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    //下载文件
    @Test
    public void tesrDownload(){
        try{
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            //定义trackerclient trackserver
            TrackerClient trackerClient=new TrackerClient();
            //链接tracker
            TrackerServer connection = trackerClient.getConnection();
            //获取storage
            StorageServer storeStorage = trackerClient.getStoreStorage(connection);
            //创建storageclient
            StorageClient1 storageClient1=new StorageClient1(connection,storeStorage);
            String fileId="group1/M00/00/00/wKgZhV3amgOAKNLtAAIAT-yWXvo489.jpg";
            byte[] bytes = storageClient1.download_file1(fileId);
            FileOutputStream fileOutputStream=new FileOutputStream(new File("E:\\新建文件夹\\KD.jpg"));
            fileOutputStream.write(bytes);
        }catch (Exception e){
        e.printStackTrace();
        }
    }
}
