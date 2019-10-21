package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;

public class Consumer_sms_02 {
    private static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    //交换机
    private static final String EXCHANGE_FANOUT_INFORM="exchange_fanout_inform";
    public static void main(String[] args) throws Exception{
        //与mq连接，通过连接工厂创建
        ConnectionFactory connectionFactory=new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        //设置虚拟机,y一个mq可以设置多个虚拟机，每个虚拟机相当于一个独立的mq
        connectionFactory.setVirtualHost("/");
        Connection connection=null;
        //建立新连接
        connection=connectionFactory.newConnection();
        //创建会话通道
        Channel channel = connection.createChannel();
        //声明队列
        channel.queueDeclare(QUEUE_INFORM_SMS,true,false,false,null);
        //绑定交换机
        channel.exchangeDeclare(EXCHANGE_FANOUT_INFORM, BuiltinExchangeType.FANOUT);

        //坚挺队列
        /**
         * 1 队列名称
         * 2 Autoask 自动回复mq ,设置为true为自动回复
         * 3 callback：消费方法，当消费者接收到消息要执行的方法
         *
         */
        //实现消费方法
        DefaultConsumer defaultConsumer=new DefaultConsumer(channel){
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                //1 consumerTag 消费者标签，用来标识消费者·，在监听队列时设置
                //2 envelope:信封，
                //3 消息属性
                //4 消息
                //获得交换机
                String exchange = envelope.getExchange();
                //获得消息在通道中的id
                long tag = envelope.getDeliveryTag();
                //消息内容
                String message=new String(body,"utf-8");
                System.out.println(message);
            }
        };
        //监听队列
        channel.basicConsume(QUEUE_INFORM_SMS,true,defaultConsumer);
    }

}
