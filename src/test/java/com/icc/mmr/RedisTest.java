package com.icc.mmr;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.UUID;
import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @Author K
 * @Date 2020/5/25 12:31
 * @Version 1.0
 */
public class RedisTest {
    private static  Integer inventory = 1001;
    private static final int NUM = 1000;
    private static LinkedBlockingQueue linkedBlockingQueue = new LinkedBlockingQueue();
    static RedisLock redisLock = new RedisLock();

    public static void main(String[] args) {
           ThreadPoolExecutor threadPoolExecutor =
                   new ThreadPoolExecutor(inventory, inventory, 10L, SECONDS, linkedBlockingQueue);
           long start = System.currentTimeMillis();
            for (int i = 0; i <= NUM; i++) {
           threadPoolExecutor.execute(new Runnable() {
                public void run() {
                        String lock = redisLock.lock(UUID.randomUUID().toString() );
                    try {
                        if (lock != null) {
                            System.out.println(Thread.currentThread().getName()+"---拿"+lock+"上锁成功");
                            inventory--;
                            System.out.println(inventory);
                        } else {
                            System.out.println(Thread.currentThread().getName()+"---上锁失败");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        if (lock == null) {
                            lock = "";
                        }
                        boolean unlock = redisLock.unlock(lock);
                        if (unlock) {
                            System.out.println(Thread.currentThread().getName()+"---拿"+lock+"解锁成功");
                        }
                    }
               }
           });
        }
       long end = System.currentTimeMillis();
       System.out.println("执行线程数:" + NUM + "   总耗时:" + (end - start) + "  库存数为:" + inventory);
   }

}
