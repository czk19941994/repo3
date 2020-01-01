package com.xuecheng.manage_media.service;

import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.domain.media.response.MediaCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import jdk.management.resource.ResourceRequest;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Service
public class MediaUploadService {
    @Autowired
    private MediaFileRepository mediaFileRepository;
    @Value("${xc-service-manage-media.upload-location}")
    private String upload_location;
    //文件上传注册，检查文件是否存在
    public ResponseResult register(String fileMd5, String fileName, Long fileSize, String mimeType, String fileExt) {
        /**
         * 根据文件md5得到文件路径
         * 规则：
         * 一级目录：md5的第一个字符
         * 二级目录：md5的第二个字符
         * 三级目录：md5
         * 文件名：md5+文件扩展名
         * @param fileMd5 文件md5值
         * @param fileExt 文件扩展名
         * @return 文件路径
         */
        //检查文件是否在在磁盘上存在
        //文件所属目录的路径
        String fileFolderPath = getFileFolderPath(fileMd5);
        String filePath = getFilePath(fileMd5, fileExt);
        File file=new File(filePath);
        //是否存在
        boolean exists = file.exists();
        //检查文件信息在mongodb中是否存在
        Optional<MediaFile> byId = mediaFileRepository.findById(fileMd5);
        if (exists&&byId.isPresent()){
            //文件存在
            ExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_EXIST);
        }
        //文件不存在
        //创建文件目录
        File fileFolder=new File(fileFolderPath);
        if (!fileFolder.exists()){
            fileFolder.mkdir();
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }
    //得到文件所属目录的路径
    private String getFileFolderPath(String fileMd5){
        return upload_location+fileMd5.substring(0,1)+"/"+fileMd5.substring(1,2)+"/"+fileMd5+"/";
    }
    //得到文件的目录
    private String getFilePath(String fileMd5,String fileExt){
        return upload_location+fileMd5.substring(0,1)+"/"+fileMd5.substring(1,2)+"/"+fileMd5+"/"+fileMd5+"."+fileExt;
    }

    /**
     * 分块文件检查
     * @param fileMd5
     * @param chunk
     * @param chunkSize
     * @return
     */
    public CheckChunkResult checkChunk(String fileMd5, Integer chunk, Integer chunkSize) {
        String chunkFileFolder = getChunkFileFolder(fileMd5);
        //块文件
        File chunkFile=new File(chunkFileFolder+chunk);
        if (chunkFile.exists()){
            return new CheckChunkResult(CommonCode.SUCCESS,true);
        }else {
            return new CheckChunkResult(CommonCode.SUCCESS,false);
        }
    }

    /**
     * 块文件目录
     * @param fileMd5
     * @return
     */
    private String getChunkFileFolder(String fileMd5){
        return upload_location+fileMd5.substring(0,1)+"/"+fileMd5.substring(1,2)+"/"+fileMd5+"/chunk";
    }

    /**
     * 上传分块
     * @param file
     * @param fileMd5
     * @param chunk
     * @return
     */
    public ResponseResult uploadChunk(MultipartFile file, String fileMd5, Integer chunk) {
        //检查分块目录，目录不存在则创建
        //得到分块目录
        String chunkFileFolder = getChunkFileFolder(fileMd5);
        File fileFolder=new File(chunkFileFolder);
        if (!fileFolder.exists()){
            fileFolder.mkdir();
        }
        String chunkFilePath=chunkFileFolder+chunk;
        //上传文件输入流
        FileOutputStream fileOutputStream=null;
        try {
            InputStream inputStream = file.getInputStream();
            fileOutputStream=new FileOutputStream(new File(chunkFilePath));
            IOUtils.copy(inputStream,fileOutputStream);
        }catch (IOException ex){
            ex.printStackTrace();
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 块文件的合并
     * @param fileMd5
     * @param fileName
     * @param fileSize
     * @param mimeType
     * @param fileExt
     * @return
     */
    public ResponseResult mergeChunk(String fileMd5, String fileName, Long fileSize, String mimeType, String fileExt) {
        //合并所有分块
        //得到分块文件的目录
        String chunkFileFolderPath = getChunkFileFolder(fileMd5);
        File chunkFileFolder=new File(chunkFileFolderPath);
        //分块文件列表
        File[] files = chunkFileFolder.listFiles();
        //c创建一个合并文件
        String filePath = getFilePath(fileMd5, fileExt);
        File mergeFile=new File(filePath);
        //执行合并
        List<File> files1 = Arrays.asList(files);
        try {
            File file = mergeFile(files1, mergeFile);
            if (file==null){
                 ExceptionCast.cast(MediaCode.MERGE_FILE_FAIL);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //校验文件的md5和前端传入的md5是否一致
        boolean b = checkFile(mergeFile, fileMd5);
        if (!b){
            ExceptionCast.cast(MediaCode.MERGE_FILE_FAIL);
        }
        //将文件信息写入mongodb
        MediaFile mediaFile=new MediaFile();
        mediaFile.setFileId(fileMd5);
        mediaFile.setFileName(fileMd5+"."+fileExt);
        mediaFile.setFileOriginalName(fileName);
        //文件保存的相对路径
        String filePath1="fileMd5.substring(0,1)+\"/\"+fileMd5.substring(1,2)+\"/\"+fileMd5+\"/\"+fileMd5+\".\"+fileExt";
        mediaFile.setFilePath(filePath1);
        mediaFile.setFileSize(fileSize);
        mediaFile.setUploadTime(new Date());
        mediaFile.setMimeType(mimeType);
        mediaFile.setFileType(fileExt);
        mediaFile.setFileStatus("301002");
        mediaFileRepository.save(mediaFile);
        return new ResponseResult(CommonCode.SUCCESS);
    }
    private File mergeFile(List<File> chunkFileList,File mergeFile) throws IOException{
        if (mergeFile.exists()){
            //文件存在则删除
            mergeFile.delete();
        }else {
            //创建一个新文件
            mergeFile.createNewFile();
        }
        Collections.sort(chunkFileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (Integer.parseInt(o1.getName())>Integer.parseInt(o2.getName())){
                    return 1;
                }
                return -1;
            }
        });
        //创建写对象
        RandomAccessFile raf_write=new RandomAccessFile(mergeFile,"rw");
        byte[] bytes=new byte[1024];
        for (File file : chunkFileList) {
            RandomAccessFile raf_read=new RandomAccessFile(file,"r");
            int len=-1;
            while ((len=raf_read.read(bytes))!=-1){
                raf_write.write(bytes,len,0);
            }
            raf_read.close();
        }
        raf_write.close();
        return mergeFile;
    }
    //
    private boolean checkFile(File mergeFile,String md5) {

        try {
            //创建文件输入流
            FileInputStream inputStream=new FileInputStream(mergeFile);
            //得到文件md5
            String md5Hex = DigestUtils.md5Hex(inputStream);
            if (md5.equalsIgnoreCase(md5Hex)){
                return true;
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }
        return false;
    }
}
