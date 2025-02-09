package com.ecommerce.flashsale.db.dao;


import com.ecommerce.flashsale.db.mappers.SeckillActivityMapper;
import com.ecommerce.flashsale.db.po.SeckillActivity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
@Slf4j
@Repository
public class  SeckillActivityDaoImpl implements SeckillActivityDao {

    @Resource
    private SeckillActivityMapper seckillActivityMapper;

    @Override
    public List<SeckillActivity> querySeckillActivitysByStatus(int activityStatus) {
        return seckillActivityMapper.querySeckillActivitysByStatus(activityStatus);
    }

    @Override
    public void inertSeckillActivity(SeckillActivity seckillActivity) {
        seckillActivityMapper.insert(seckillActivity);
    }

    @Override
    public SeckillActivity querySeckillActivityById(long activityId) {
        return seckillActivityMapper.selectByPrimaryKey(activityId);
    }

    @Override
    public void updateSeckillActivity(SeckillActivity seckillActivity) {
        seckillActivityMapper.updateByPrimaryKey(seckillActivity);
    }
    @Override
    public boolean lockStock(Long seckillActivityId){
        int res = seckillActivityMapper.lockStock(seckillActivityId);
        if(res < 1){
            log.error("fail");
            return false;
        }
        System.out.println("res success");
        return true;
    }
    @Override
    public boolean deductStock(Long seckillActivityId){
        int res = seckillActivityMapper.deductStock(seckillActivityId);
        if(res < 1){
            log.error("fail");
            return false;
        }
        System.out.println("res success");
        return true;
    }
    @Override
    public void revertStock(Long seckillActivityId){
        seckillActivityMapper.revertStock(seckillActivityId);
    }
}
