package com.ecommerce.flashsale.web;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class TestController {

    @ResponseBody
    @RequestMapping("hello")
    public String hello(){
        String result;
        try (Entry entry = SphU.entry("HelloResource")){

            result  = "Hello Sentinel";
            return result;
        }catch (BlockException ex) {

            log.error(ex.toString());
            result = "try again";
            return  result;
        }
    }

    @PostConstruct
    public void seckillsFlow(){

        List<FlowRule> rules = new ArrayList<>();

        FlowRule rule = new FlowRule();

        rule.setResource("seckills");

        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);

        rule.setCount(1);

        FlowRule rule2 = new FlowRule();
        rule2.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule2.setCount(2);
        rule2.setResource("HelloResource");

        rules.add(rule);
        rules.add(rule2);

        FlowRuleManager.loadRules(rules);
    }
}
