package com.ecommerce.flashsale.service;

import com.alibaba.fastjson.JSON;
import com.ecommerce.flashsale.db.dao.OrderDao;
import com.ecommerce.flashsale.db.dao.SeckillActivityDao;
import com.ecommerce.flashsale.db.dao.SeckillCommodityDaoImpl;
import com.ecommerce.flashsale.db.po.Order;
import com.ecommerce.flashsale.db.po.SeckillActivity;
import com.ecommerce.flashsale.db.po.SeckillCommodity;
import com.ecommerce.flashsale.mq.RocketMQService;
import com.ecommerce.flashsale.util.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@Service
public class SeckillActivityService {
    @Resource
    private RedisService redisService;
    @Resource
    private SeckillActivityDao seckillActivityDao;
    @Resource
    private RocketMQService rocketMQService;
    @Resource
    OrderDao orderDao;

    private SnowFlake snowFlake = new SnowFlake(1, 1);
    @Autowired
    private SeckillCommodityDaoImpl seckillCommodityDaoImpl;

    public boolean seckillStockValidator(long activityId){
        String key = "stock:" + activityId;
        System.out.println("key is:" + key);
        return redisService.stockDeductValidation(key);
    }

    public Order createOrder(long activityId,long userId) throws Exception{
        SeckillActivity seckillActivity = seckillActivityDao.querySeckillActivityById(activityId);
        Order order = new Order();
        order.setOrderNo(String.valueOf(snowFlake.nextId()));
        order.setSeckillActivityId(seckillActivity.getId());
        order.setUserId(userId);
        order.setOrderAmount(seckillActivity.getSeckillPrice().longValue());
        rocketMQService.sendMessage("seckill_order", JSON.toJSONString(order));
        System.out.println("seckillActivity:" + JSON.toJSONString(order));
        rocketMQService.sendDelayMessage("pay_check", JSON.toJSONString(order),5);
        return order;

    }

    public void payOrderProceess(String orderNo) throws Exception {
        Order order = orderDao.queryOrders(orderNo);
        if (order == null){
            log.info("cant find orderNo" + orderNo);
            return;
        }
        order.setPayTime(new Date());
        order.setOrderStatus(2);
        orderDao.updateOrder(order);
        rocketMQService.sendMessage("pay_done", JSON.toJSONString(order));
    }

    public void pushSeckillInfoToRedis(long seckillActivityId){
        SeckillActivity seckillActivity = seckillActivityDao.querySeckillActivityById(seckillActivityId);
        redisService.setValue("seckillActivity:" +  seckillActivityId,JSON.toJSONString(seckillActivity));
        SeckillCommodity seckillCommodity = seckillCommodityDaoImpl.querySeckillCommodityById(seckillActivity.getCommodityId());
        redisService.setValue("seckillCommodity:" + seckillActivity.getCommodityId(),JSON.toJSONString(seckillCommodity));

    }
}
