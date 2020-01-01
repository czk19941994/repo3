package com.xuecheng.manage_media;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class Media {
    @Test
    public void testTrunk() throws IOException{
        File sourceFile=new File("");
        String trunkPath="";
        File chunkFolder=new File(trunkPath);
        if (!chunkFolder.exists()){
            chunkFolder.mkdir();
        }
        //设置分亏大小
        long trunkSize=1024*1024*1;
        //分块数量
        long trunkNum=(long)Math.ceil(sourceFile.length()*1.0/trunkSize);
        if(trunkNum<=0){
            trunkNum=1;
        }
        byte[] bytes=new byte[1024];
        RandomAccessFile raf_read=new RandomAccessFile(sourceFile,"r");
        //分块
        for (int i=0;i<trunkNum;i++){
            //创建分块文件
            File file=new File(trunkPath+i);
            //创建文件
            boolean newFile = file.createNewFile();
            if (newFile){
                //像分块文件中写数据
                RandomAccessFile raf_write=new RandomAccessFile(file,"rw");
                int len=-1;
                while ((len=raf_read.read(bytes))!=-1){
                    raf_write.write(bytes,len,0);
                    if (file.length()>trunkSize){
                        break;
                    }
                }
                raf_write.close();
            }
        }
        raf_read.close();
    }
    @Test//测试文件合并
    public void testMergeFile() throws IOException{
        //块文件目录
        String chunkFilePath="";
        File chunkFileFolder=new File(chunkFilePath);
        //块文件列表
        File[] files = chunkFileFolder.listFiles();
        //合并文件
        File mergeFile=new File("");
        //将块文件排序，按名称升序
        List<File> files1 = Arrays.asList(files);
        Collections.sort(files1, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (Integer.parseInt(o1.getName())>Integer.parseInt(o2.getName())){
                    return 1;
                }
                return -1;
            }

        });
        //创建新文件
        boolean newFile = mergeFile.createNewFile();
        //创建写对象
        RandomAccessFile raf_write=new RandomAccessFile(mergeFile,"rw");
        byte[] bytes=new byte[1024];
        for (File file : files) {
            //创建读取文件对象
            RandomAccessFile raf_read=new RandomAccessFile(file,"r");
            int len=-1;
            while ((len=raf_read.read(bytes))!=-1){
                raf_write.write(bytes,len,0);
            }
            raf_read.close();
        }
        raf_write.close();
    }
}
