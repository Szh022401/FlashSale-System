package com.ecommerce.flashsale.service;

import com.ecommerce.flashsale.db.dao.SeckillActivityDao;
import com.ecommerce.flashsale.db.dao.SeckillCommodityDao;
import com.ecommerce.flashsale.db.po.SeckillActivity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SeckillOverSellService {
    @Resource
    private SeckillActivityDao seckillActivityDao;
    private int count1 =1;
    private int count2 =1;
    public String processFlashSale(long activityId){
        SeckillActivity activity = seckillActivityDao.querySeckillActivityById(activityId);
        int availableStock = activity.getAvailableStock();
        String result;

        if(availableStock >= 0){
            result = "success";
            System.out.println(count1 + " "+ result);
            count1++;
            availableStock--;
            activity.setAvailableStock(availableStock);
            seckillActivityDao.updateSeckillActivity(activity);
        }else{
            result = "fail";
            System.out.println(count2 + " "+ result);
            count2++;
        }
        return result;
    }
}
