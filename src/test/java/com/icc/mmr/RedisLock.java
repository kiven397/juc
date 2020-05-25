package com.icc.mmr;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import java.util.Collections;


/**
 * @Author K
 * @Date 2020/5/25 12:07
 * @Version 1.0
 */
@Component
public class RedisLock {
    private String LOCK_KEY = "redis_lock";

    protected long INTERNAL_LOCK_LEASE_TIME = 10000;

    private long timeout = 1000;

    private SetParams params = SetParams.setParams().nx().px(INTERNAL_LOCK_LEASE_TIME);

    JedisPool jedisPool = new JedisPool("192.168.31.40", 6379);

    public String  lock(String id) {
        Long start = System.currentTimeMillis();
        Jedis jedis = jedisPool.getResource();
        try {
            for (; ; ) {
                //SET命令返回OK ，则证明获取锁成功
                String lock = jedis.set(LOCK_KEY, id, params);
                if ("OK".equals(lock)) {
                    return id;
                }
            //否则循环等待，在timeout时间内仍未获取到锁，则获取失败
            long l = System.currentTimeMillis() - start;
            if (l >= timeout) {
                return null;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            }
        } finally {
            jedis.close();
        }
    }

    public boolean unlock(String id) {
        Jedis jedis = jedisPool.getResource();
        String script =
                "if redis.call('get',KEYS[1]) == ARGV[1] then" +
                        "   return redis.call('del',KEYS[1]) " +
                        "else" +
                        "   return 0 " +
                        "end";
        try {
            String result = jedis.eval(script, Collections.singletonList(LOCK_KEY), Collections.singletonList(id)).toString();
            return "1".equals(result) ? true : false;
        } finally {
            jedis.close();
        }
    }
}
