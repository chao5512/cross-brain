package com.dataset.management.util;

import com.dataset.management.config.JedisConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collections;

@Component
public class JedisUtils {
    @Autowired
    private JedisConfig jedisConfig;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";//PX表示超时时间是毫秒设置，EX表示超时时间是分钟设置
    private static final Long RELEASE_SUCCESS = 1L;

    private JedisPool getJedisPool(){
        return jedisConfig.redisPoolFactory();
    }

    /**
     * 尝试获取分布式锁
     * @param lockKey 锁
     * @param requestId 请求标识
     * @param expireTime 超期时间
     * @return 是否获取成功
     */
    public boolean tryGetDistributedLock(String lockKey, String requestId, int expireTime) {
        JedisPool jedisPool = getJedisPool();
        //从连接池获取连接
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
            if (LOCK_SUCCESS.equals(result)) {
                return true;
            }
            return false;
        }catch(Exception e) {
            e.printStackTrace();
            return false;
        }finally{
            //归还连接到redis池中
            jedis.close();
        }
    }

    /**
     * 释放分布式锁
     * @param lockKey 锁
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    public boolean releaseDistributedLock(String lockKey, String requestId) {
        JedisPool jedisPool = getJedisPool();
        //从连接池获取连接
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
            if (RELEASE_SUCCESS.equals(result)) {
                return true;
            }
            return false;
        }catch(Exception e) {
            e.printStackTrace();
            return false;
        }finally{
            //归还连接到redis池中
            jedis.close();
        }
    }

}
