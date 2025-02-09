package com.ecommerce.flashsale.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.Collections;
@Slf4j
@Service
public class RedisService {
    @Resource
    private JedisPool jedisPool;

    public RedisService setValue(String key, Long value) {
        try (Jedis client = jedisPool.getResource()) {
            client.set(key, value.toString());
        } catch (Exception e) {
            System.out.println("Failed to set value in Redis: " + e.getMessage());
        }
        return this;
    }
    public void setValue(String key, String value) {
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.set(key, value);
        jedisClient.close();
    }

    public String getValue(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        try (Jedis client = jedisPool.getResource()) {
            String value = client.get(key);
            return value;
        } catch (Exception e) {
            System.out.println("Failed to get value from Redis: " + e.getMessage());
            return null;
        }
    }

    public boolean stockDeductValidation(String key) {
        System.out.println("here is " + key);
        String value = getValue(key);

        if (value == null) {
            System.out.println("Value for key " + key + " is null");
            return false;
        }else{
            System.out.println("Value for key " + key + " is " + value);
        }
        try (Jedis client = jedisPool.getResource()) {
            String script = "if redis.call('exists', KEYS[1]) == 1 then\n" +
                    "   local stock = tonumber(redis.call('get', KEYS[1]))\n" +
                    "   if stock == 0 then\n" +
                    "       return -1\n" +
                    "   end\n" +
                    "   redis.call('decr', KEYS[1])\n" +
                    "   return stock - 1\n" +
                    "end\n" +
                    "return -1";
            Long stock = (Long) client.eval(script, Collections.singletonList(key), Collections.emptyList());
            System.out.println("stock is " + stock);

            if (stock < 0) {
                System.out.println("not enough stock");
                return false;
            } else {
                System.out.println("success");
                return true;
            }
        } catch (Throwable throwable) {
            System.out.println("fail reason " + throwable.toString());
            return false;
        }
    }
    public void addLimitMember(long activityId,long userID){
        Jedis jedis = jedisPool.getResource();
        jedis.sadd("seckillActivity_users" + activityId,String.valueOf(userID));
    }
    public boolean isInLimitMember(long activityId,long userID){
        Jedis jedis = jedisPool.getResource();
        boolean sismember = jedis.sismember("seckillActivity_users" + activityId,String.valueOf(userID));
        log.info("user id:{} activityId: {} ", userID, activityId);
        return sismember;
    }
    public void removeLimitMember(long activityId,long userID){
        Jedis jedis = jedisPool.getResource();
        jedis.srem("seckillActivity_users" + activityId,String.valueOf(userID));
    }
    public void revertStock(String key) {
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.incr(key);
        jedisClient.close();
    }
    public  boolean tryGetDistributedLock(String lockKey, String requestId, int expireTime) {
        Jedis jedisClient = jedisPool.getResource();
        String result = jedisClient.set(lockKey, requestId, "NX", "PX", expireTime);
        jedisClient.close();
        return "OK".equals(result);
    }
    public boolean releaseDistributedLock(String lockKey, String requestId) {
        Jedis jedisClient = jedisPool.getResource();
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Long result = (Long) jedisClient.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
        jedisClient.close();
        return result == 1L;
    }
}
