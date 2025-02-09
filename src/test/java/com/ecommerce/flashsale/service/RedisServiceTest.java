package com.ecommerce.flashsale.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
class RedisServiceTest {
    @Resource
    private RedisService redisService;
    @Test
    void setValue() {
        String value = redisService.setValue("test1",100L).getValue("test1");
        assertEquals(new Long(value),100L);
    }

    @Test
    void getValue() {
        String value = redisService.getValue("test1");
        assertEquals(new Long(value),100L);
    }

    @Test
    void stockDeductValidation() {
        boolean result = redisService.stockDeductValidation("test1");
        assertTrue(result);
        String value = redisService.getValue("test1");
        assertEquals(new Long(value),99L);
    }
}