package com.ecommerce.flashsale.web;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.fastjson.JSON;
import com.ecommerce.flashsale.db.dao.OrderDao;
import com.ecommerce.flashsale.db.dao.SeckillActivityDao;
import com.ecommerce.flashsale.db.dao.SeckillCommodityDao;
import com.ecommerce.flashsale.db.po.Order;
import com.ecommerce.flashsale.db.po.SeckillActivity;
import com.ecommerce.flashsale.db.po.SeckillCommodity;
import com.ecommerce.flashsale.service.RedisService;
import com.ecommerce.flashsale.service.SeckillActivityService;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;


@Slf4j
@Controller
public class SeckillActivityController {
    @Resource
    private OrderDao orderDao;
    @Resource
    private  SeckillActivityDao seckillActivityDao;
    @Resource
    private SeckillCommodityDao seckillCommodityDao;
    @Resource
    private SeckillActivityService seckillActivityService;
    @Autowired
    private RedisService redisService;

    @RequestMapping("/addSeckillActivity")
    public String addSeckillActivity() {
        return "add_activity";
    }
    @RequestMapping("/addSeckillActivityAction")
    public String addSeckillActivityAction(@RequestParam("name")String name,
                                           @RequestParam("commodityId") long commodityId,
                                           @RequestParam("oldPrice")BigDecimal oldPrice,
                                           @RequestParam("seckillPrice") BigDecimal seckillPrice,
                                           @RequestParam("seckillNumber")long seckillNumber,
                                           @RequestParam("startTime")String startTime,
                                           @RequestParam("endTime") String endTime,
                                           Map<String, SeckillActivity> map )  throws ParseException {
        startTime = startTime.substring(0, 10) + startTime.substring(11);
        endTime = endTime.substring(0, 10) + endTime.substring(11);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-ddhh:mm");
        SeckillActivity seckillActivity = new SeckillActivity();
        seckillActivity.setName(name);
        seckillActivity.setCommodityId(commodityId);
        seckillActivity.setSeckillPrice(seckillPrice);
        seckillActivity.setOldPrice(oldPrice);
        seckillActivity.setTotalStock(seckillNumber);
        seckillActivity.setAvailableStock(new Integer("" + seckillNumber));
        seckillActivity.setLockStock(0L);
        seckillActivity.setActivityStatus(1);
        seckillActivity.setStartTime(format.parse(startTime));
        seckillActivity.setEndTime(format.parse(endTime));
        seckillActivityDao.inertSeckillActivity(seckillActivity);
        map.put("seckillActivity",seckillActivity);
        return "add_success";
    }
    @RequestMapping("/seckills")
    public String activityList(Map<String, List<SeckillActivity>> map){
        try (Entry entry = SphU.entry("seckills")){
            List<SeckillActivity> seckillActivities = seckillActivityDao.querySeckillActivitysByStatus(1);
            map.put("seckillActivities",seckillActivities);
            return "seckill_activity";
        }catch (BlockException ex) {
            log.error(ex.toString());
            return  "wait";
        }
    }

    @PostConstruct
    public void seckillsFlow(){

        List<FlowRule> rules = new ArrayList<>();

        FlowRule rule = new FlowRule();

        rule.setResource("seckills");

        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);

        rule.setCount(1);
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }



    @RequestMapping("/item/{seckillActivityId}")
    public String itemPage(@PathVariable("seckillActivityId") long seckillActivityId,Map<String,Object> map){
        SeckillActivity seckillActivity;
        SeckillCommodity seckillCommodity;

        String seckillActivityInfo = redisService.getValue("seckillActivity:" + seckillActivityId);
        if(StringUtils.isNotEmpty(seckillActivityInfo)){
            log.info("redis info" + seckillActivityInfo);
            seckillActivity = JSON.parseObject(seckillActivityInfo, SeckillActivity.class);
        }else {
            seckillActivity = seckillActivityDao.querySeckillActivityById(seckillActivityId);
        }
        String seckillCommodityInfo = redisService.getValue("seckillCommodity:" + seckillActivity.getCommodityId());
        if(StringUtils.isNotEmpty(seckillCommodityInfo)){
            log.info("redis info" + seckillCommodityInfo);
            seckillCommodity = JSON.parseObject(seckillCommodityInfo, SeckillCommodity.class);
        }else {
            seckillCommodity = seckillCommodityDao.querySeckillCommodityById(seckillActivity.getCommodityId());
        }
        map.put("seckillActivity",seckillActivity);
        map.put("seckillCommodity",seckillCommodity);
        map.put("seckillPrice",seckillActivity.getSeckillPrice());
        map.put("oldPrice",seckillActivity.getOldPrice());
        map.put("commodityId",seckillActivity.getCommodityId());
        map.put("commodityName",seckillCommodity.getCommodityName());
        map.put("commodityDesc",seckillCommodity.getCommodityDesc());
        return "seckill_item";
    }
    @ResponseBody
    @RequestMapping("/seckill/buy/{userId}/{seckillActivityId}")
    public ModelAndView seckillCommodity(@PathVariable long userId, @PathVariable long seckillActivityId) {
        boolean stockValidateResult = false;
        System.out.println("use id is " + userId);
        ModelAndView modelAndView = new ModelAndView();

        try {
            if(redisService.isInLimitMember(seckillActivityId,userId)){
                modelAndView.addObject("resultInfo","sorry you are not in list");
                modelAndView.setViewName("seckill_result");
                return modelAndView;
            }
            stockValidateResult = seckillActivityService.seckillStockValidator(seckillActivityId);
            System.out.println("stockValidateResult " + stockValidateResult);
            if (stockValidateResult) {
                Order order = seckillActivityService.createOrder(seckillActivityId, userId);
                log.info("pass");
                modelAndView.addObject("resultInfo","success,order id is  " + order.getOrderNo());
                modelAndView.addObject("orderNo",order.getOrderNo());
                redisService.addLimitMember(seckillActivityId,userId);
            }else {
                modelAndView.addObject("result info order","fail");
            }
        }catch (Exception e){
            log.error("fail: ", e.toString());
            modelAndView.addObject("resultInfo","fail");
        }

        modelAndView.setViewName("seckill_result");
        return modelAndView;
    }
    @RequestMapping("/seckill/orderQuery/{orderNo}")
    public ModelAndView orderQuery(@PathVariable String orderNo) {
        log.info("orderNumber：" + orderNo);
        Order order = orderDao.queryOrders(orderNo);
        ModelAndView modelAndView = new ModelAndView();

        if (order != null) {
            modelAndView.setViewName("order");
            modelAndView.addObject("order", order);
            SeckillActivity seckillActivity = seckillActivityDao.querySeckillActivityById(order.getSeckillActivityId());
            modelAndView.addObject("seckillActivity", seckillActivity);
        } else {
            modelAndView.setViewName("wait");
        }
        return modelAndView;
    }
    @RequestMapping("/seckill/payOrder/{orderNo}")
    public String payOrder(@PathVariable String orderNo) throws Exception {
        seckillActivityService.payOrderProceess(orderNo);
        return "redirect:/seckill/orderQuery/" + orderNo;
    }
    @ResponseBody
    @RequestMapping("/seckill/getSystemTime")
    public String getSystemTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(new Date());
        return date;
    }
}
