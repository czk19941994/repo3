package com.xuecheng.manage_media_process.mq;

import com.alibaba.fastjson.JSON;

import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.MediaFileProcess_m3u8;
import com.xuecheng.framework.utils.HlsVideoUtil;
import com.xuecheng.framework.utils.Mp4VideoUtil;
import com.xuecheng.manage_media_process.dao.MediaFileRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Component
@SuppressWarnings("all")
public class MediaProcessTask {
    @Value("${xc-service-manage-media.mq.ffmpeg-path}")
    private String ffmpeg_path;
    //上传文件根目录
    @Value("${xc-service-manage-media.video -location}")
    private String serverPath;
    @Autowired
    private MediaFileRepository mediaFileRepository;
    //消费者并发数量
    public static final int DEFAULT_CONCURRENT = 10;
    @RabbitListener(queues = "${xc-service-manage-media.mq.queue-media-video-processor}",containerFactory = "customContainerFactory")
    public void receiveMediaProcessTask(String msg){
        //解析消息内容，得到media id
        Map map = JSON.parseObject(msg, Map.class);
        String mediaId=(String)map.get("mediaId");

        //拿mediaId从数据库查询文件
        Optional<MediaFile> optional = mediaFileRepository.findById(mediaId);
        if (!optional.isPresent()){
            return;
        }
        MediaFile mediaFile = optional.get();
        String fileType = mediaFile.getFileType();
        if (!fileType.equals("avi")){
            mediaFile.setProcessStatus("303004");//将视频状态额更新为无需处理
            mediaFileRepository.save(mediaFile);
            return;
        }else {
            //更新为处理中
            mediaFile.setProcessStatus("303001");//处理中
            mediaFileRepository.save(mediaFile);
        }
        //使用工具类将文件从avi转成mp4
        String video_path=serverPath+mediaFile.getFilePath()+mediaFile.getFileName();
        String mp4_name=mediaFile.getFileId()+".mp4";
        String mp4folder_name=serverPath+mediaFile.getFilePath();
        Mp4VideoUtil mp4VideoUtil=new Mp4VideoUtil(ffmpeg_path,video_path,mp4_name,mp4folder_name);
        //进行处理
        String result = mp4VideoUtil.generateMp4();
        if (result==null||!result.equals("success")){
            //处理失败
            mediaFile.setProcessStatus("303003");
            //定义记录失败原因
            MediaFileProcess_m3u8 mediaFileProcess_m3u8=new MediaFileProcess_m3u8();
            mediaFileProcess_m3u8.setErrormsg(result);
            mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
            mediaFileRepository.save(mediaFile);
            return;
        }
        //将mp4转成m3u8和ts文件
        String mp4Video_path=serverPath+mediaFile.getFilePath()+mp4_name;
        //m3u8文件名称
        String m3u8_name=mediaFile.getFileId()+".m3u8";
        //m3u8文件目录
        String m3u8folder_path=serverPath+mediaFile.getFilePath()+"hls/";
        HlsVideoUtil hlsVideoUtil=new HlsVideoUtil(ffmpeg_path,mp4Video_path,m3u8_name,mp4folder_name);
        //生成m3u8及ts文件
        String s = hlsVideoUtil.generateM3u8();
        if (s==null||!s.equals("success")){
            //处理失败
            mediaFile.setProcessStatus("303003");
            //定义记录失败原因
            MediaFileProcess_m3u8 mediaFileProcess_m3u8=new MediaFileProcess_m3u8();
            mediaFileProcess_m3u8.setErrormsg(s);
            mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
            mediaFileRepository.save(mediaFile);
            return;
        }
        //获取ts文件列表
        List<String> ts_list = hlsVideoUtil.get_ts_list();

        //处理成功
        mediaFile.setProcessStatus("303002");
        MediaFileProcess_m3u8 mediaFileProcess_m3u8=new MediaFileProcess_m3u8();
        mediaFileProcess_m3u8.setTslist(ts_list);
        mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
        //保存文件url（视频播放的相对路径）
        String fileUrl=mediaFile.getFilePath()+"hls/"+m3u8_name;
        mediaFile.setFileUrl(fileUrl);
        mediaFileRepository.save(mediaFile);
    }

    /**
     * rabbitmq的并发设置，可以同时处理的数量
     * @param configurer
     * @param connectionFactory
     * @return
     */
    @Bean("customContainerFactory")
    public SimpleRabbitListenerContainerFactory
    containerFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer, ConnectionFactory
            connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConcurrentConsumers(DEFAULT_CONCURRENT);
        factory.setMaxConcurrentConsumers(DEFAULT_CONCURRENT);
        configurer.configure(factory, connectionFactory);
        return factory;
    }
}
