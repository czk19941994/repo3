package com.xuecheng.test.rabbitmq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class Spring_Rabbitmq {
    //邮件队列
    public static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    //短信队列
    public static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    //交换机
    public static final String EXCHANGE_TOPICS_INFORM="exchange_topics_inform";
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Test
    public void sendEmail(){
        /**
         * 参数 1 交换机名称
         * 2 routingkey
         * 3 Object 发送的消息
         */
        rabbitTemplate.convertAndSend(EXCHANGE_TOPICS_INFORM,"routingkey_email","陈彰坤");
    }
}
