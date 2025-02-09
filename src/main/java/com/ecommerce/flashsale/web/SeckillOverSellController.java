package com.ecommerce.flashsale.web;

import com.ecommerce.flashsale.service.SeckillActivityService;
import com.ecommerce.flashsale.service.SeckillOverSellService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class SeckillOverSellController {

//    @Resource
//    private SeckillOverSellService seckillOverSellService;
    @Resource
    private SeckillActivityService seckillActivityService;

//    @ResponseBody
//    @RequestMapping("/seckill/{seckillActivityId}")
//    private String seckill(@PathVariable long seckillActivityId) {
//        return seckillOverSellService.processFlashSale(seckillActivityId);
//    }

    @ResponseBody
    @RequestMapping("/seckill/{seckillActivityId}")
    public String seckillCommodity(@PathVariable long seckillActivityId) {
        boolean stockValidateResult = seckillActivityService.seckillStockValidator(seckillActivityId);
        return stockValidateResult ? "success" : "fail";

    }
}
