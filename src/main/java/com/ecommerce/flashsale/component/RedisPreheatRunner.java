package com.ecommerce.flashsale.component;

import com.ecommerce.flashsale.db.dao.SeckillActivityDao;
import com.ecommerce.flashsale.db.po.SeckillActivity;
import com.ecommerce.flashsale.service.RedisService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class RedisPreheatRunner implements ApplicationRunner {
    @Resource
    private SeckillActivityDao seckillActivityDao;
    @Resource
    private RedisService redisService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<SeckillActivity> seckillActivityList = seckillActivityDao.querySeckillActivitysByStatus(1);
        for (SeckillActivity seckillActivity : seckillActivityList) {
            System.out.println(seckillActivity.getId() + " "+ seckillActivity.getAvailableStock());
            Long id =  seckillActivity.getId();
            redisService.setValue("Stock: " + id,
                    (long) seckillActivity.getAvailableStock());
            System.out.println("id is " + id);
            System.out.println(" get" + redisService.getValue("Stock: " + id));
        }
    }
}
