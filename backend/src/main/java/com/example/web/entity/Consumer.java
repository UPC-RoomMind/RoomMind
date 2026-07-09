package com.example.web.entity;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class Consumer {
    public static void main(String[] args) throws Exception {
        // 1. 连接配置与生产者相同
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("myuser");
        factory.setPassword("secret");

        // 2. 创建连接和通道
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // 3. 声明要监听的队列（必须与生产者一致）
        String QUEUE_NAME = "hello";
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages...");

        // 4. 定义如何处理收到的消息
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
        };

        // 5. 开始消费消息
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
    }
}