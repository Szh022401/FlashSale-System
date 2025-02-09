package com.ecommerce.flashsale.mq;

import com.alibaba.fastjson.JSON;
import com.ecommerce.flashsale.db.dao.OrderDao;
import com.ecommerce.flashsale.db.dao.SeckillActivityDao;
import com.ecommerce.flashsale.db.po.Order;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RocketMQMessageListener(topic = "pay_done",consumerGroup = "pay_done_group")
public class PayDoneConsumer implements RocketMQListener<MessageExt> {
    @Resource
    private OrderDao orderDao;
    @Resource
    private SeckillActivityDao seckillActivityDao;

    @Override
    public void onMessage(MessageExt messageExt) {
        String msg = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        log.info("get create order message" + msg);
        Order order = JSON.parseObject(msg, Order.class);
        seckillActivityDao.deductStock(order.getSeckillActivityId());
    }
}
