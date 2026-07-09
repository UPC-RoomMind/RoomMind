package com.example.web.entity;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Producer {
    public static void main(String[] args) throws Exception {
        // 1. 创建连接工厂，设置 Broker 地址
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");  // 或你的 Broker IP
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setVirtualHost("/");

        // 2. 创建连接和通道
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            // 3. 声明队列（如果不存在则创建）
            // 参数：队列名, 是否持久化, 是否独占, 是否自动删除, 额外参数
            channel.queueDeclare("my-queue", false, false, false, null);

            // 4. 发送消息
            String message = "Hello RabbitMQ!";
            channel.basicPublish(
                    "",              // 交换机（空字符串表示默认交换机）
                    "my-queue",      // 路由键（这里就是队列名）
                    null,            // 消息属性
                    message.getBytes() // 消息体
            );

            System.out.println("✅ 发送成功: " + message);
        }
    }
}