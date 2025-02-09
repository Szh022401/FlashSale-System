package com.ecommerce.flashsale;

import com.ecommerce.flashsale.service.RedisService;
import com.ecommerce.flashsale.service.SeckillActivityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class RedisTest {

    @Resource
    private RedisService redisService;
    @Autowired
    private SeckillActivityService seckillActivityService;

    @Test
    public void test() {
        String value = redisService.setValue("stock:19",10L).getValue("stock:19");
        System.out.println(value);
    }
    @Test
    public void test1() {
        seckillActivityService.pushSeckillInfoToRedis(19);
    }
    @Test
    public void getSeckillActivityFromRedis() {
        String info = redisService.getValue("seckillActivity:"+19);
        System.out.println(info);
        String seckiCommodity = redisService.getValue("seckillCommodity:"+1001);
        System.out.println(seckiCommodity);
    }
}
