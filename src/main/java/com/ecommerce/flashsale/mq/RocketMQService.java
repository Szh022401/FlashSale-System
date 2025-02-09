package com.ecommerce.flashsale.mq;



import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RocketMQService {
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public void sendMessage(String topic, String body)throws Exception {
        Message message = new Message(topic,body.getBytes());
        rocketMQTemplate.getProducer().send(message);

    }
    public void sendDelayMessage(String topic, String body,int delayTime) throws Exception {
        Message message = new Message(topic,body.getBytes());
        message.setDelayTimeLevel(delayTime);
        rocketMQTemplate.getProducer().send(message);
    }
}
