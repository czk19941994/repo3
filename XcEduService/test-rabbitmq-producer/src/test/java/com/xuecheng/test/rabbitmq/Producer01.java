package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer01 {
    public static final String   QUEUE="HELLOMQ";
    public static void main(String[] args) {

        //与mq连接，通过连接工厂创建
        ConnectionFactory connectionFactory=new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        //设置虚拟机,y一个mq可以设置多个虚拟机，每个虚拟机相当于一个独立的mq
        connectionFactory.setVirtualHost("/");
        Connection connection=null;
        Channel channel=null;
        try{
            connection=connectionFactory.newConnection();
            //创建会话通道 生产者和mq都在这个channel中
             channel = connection.createChannel();
            //声明队列,队列在mq中不存在就会创建
            /**
             * 参数意义：
             * queue:队列名称
             * durable：是否持久化，如果持久化，mq后还存在
             * exclusive:是否独占连接，队列只允许在该连接中访问，如果connection连接关闭队列则自动删除，如果将此参数设置ture可用于临时队列的创建
             * autodelete：自动删除，队列不在使用时则自动删除此队列，如果将此参数和exclusive设置为true可以实现临时队列
             * argument：可以设置一个队列的扩展参数，列入存活时间；
             */
            channel.queueDeclare(QUEUE,true,false,false,null);
            //发送消息-》交换机
            /**
             * 参数意义：
             * 1 exchange：交换机，如果不指定使用默认交换机
             * 2 routingkey:路由key，交换机根据key来将消息发到制定的队列，如果使用默认交换机，routingkey设置队列名称
             * 3 perops:消息的属性
             * 4、body:消息的内容
             */
            channel.basicPublish("",QUEUE,null,"黑马程序员".getBytes());
        }catch (Exception e){
                e.printStackTrace();
        }finally {
            //关闭连接
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
    }
}
