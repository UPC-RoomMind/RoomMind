package com.example.web.rabbitqueue;

import com.example.web.dto.AppointOrderMessageDto;
import com.example.web.dto.AppointRecordDto;
import com.example.web.service.AppointRecordService;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(rollbackFor = Exception.class)
public class OrderConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderConsumer.class);

    @Autowired
    private AppointRecordService appointRecordService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleOrder(AppointOrderMessageDto message, Channel channel, Message amqpMessage) {
        long deliveryTag = amqpMessage.getMessageProperties().getDeliveryTag();

        try {
            // ✅ 从包装类中取出 OrderId 和原数据
            String orderId = message.getOrderId();
            AppointRecordDto input = message.getData();

            log.info("📩 收到预约请求, OrderId: {}", orderId);
            log.debug("预约数据: {}", input);

            // ✅ 调用原有的业务逻辑，原 DTO 完全不变
            AppointRecordDto result = appointRecordService.ToOrder(input);

            log.info("✅ 预约处理成功, OrderId: {}, RecordId: {}", orderId, result.getId());

            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("❌ 预约处理失败, OrderId: {}", message != null ? message.getOrderId() : "unknown", e);
            try {
                channel.basicNack(deliveryTag, false, true);
            } catch (Exception ex) {
                log.error("消息确认失败", ex);
            }
        }
    }
}