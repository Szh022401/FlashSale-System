package com.ecommerce.flashsale.mq;

import com.alibaba.fastjson.JSON;
import com.ecommerce.flashsale.db.dao.OrderDao;
import com.ecommerce.flashsale.db.dao.SeckillActivityDao;
import com.ecommerce.flashsale.db.po.Order;
import com.ecommerce.flashsale.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RocketMQMessageListener(topic = "pay_check", consumerGroup = "pay_check_group")
public class PayStatusCheckListener implements RocketMQListener<MessageExt> {
    @Resource
    private OrderDao orderDao;
    @Resource
    private SeckillActivityDao seckillActivityDao;
    @Resource
    private RedisService redisService;
    @Override
    public void onMessage(MessageExt messageExt) {
        String message = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        log.info("get the message :" + message);
        Order order = JSON.parseObject(message, Order.class);
        Order orderInfo = orderDao.queryOrders(order.getOrderNo());
        if(orderInfo.getOrderStatus() != 2){
            log.info("did not pay,OrderNo：" + orderInfo.getOrderNo());
            orderInfo.setOrderStatus(99);
            orderDao.updateOrder(orderInfo);
            seckillActivityDao.revertStock(order.getSeckillActivityId());
            redisService.revertStock("stock:" + order.getSeckillActivityId());
            redisService.removeLimitMember(order.getSeckillActivityId(), order.getUserId());
        }

    }
}
