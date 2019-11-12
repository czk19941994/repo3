package com.xuecheng.manage_cms_client.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.manage_cms_client.service.PageService;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 监听mq
 */
@Component
public class ConsumerPostPage {
    private static final Logger LOGGER= LoggerFactory.getLogger(ConsumerPostPage.class);
    @Autowired
    private PageService pageService;
    @RabbitListener(queues = {"${xuecheng.mq.queue}"})
    public void postPage(String msg){

       // List<String[]> list=new ArrayList<>();
        //解析消息;
        Map map = JSON.parseObject(msg, Map.class);
        //得到页面id
        String pageId = (String) map.get("pageId");
        //调用service方法存储html
        pageService.savePageToServerPath(pageId);
    }

}
