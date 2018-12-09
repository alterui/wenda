package com.nowcoder.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by ALTERUI on 2018/12/7 9:32
 */
@Service
public class JedisAdapter implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);

    private JedisPool pool;

    private Jedis jedis;

    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool("redis://localhost:6379/10");
    }

    /**
     * 添加一个元素
     * @param key
     * @param value
     * @return
     */
    public long sadd(String key, String value) {

        try {
            jedis = pool.getResource();
            return jedis.sadd(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        }
        return 0;
    }

    /**
     * 删除一个元素
     * @param key
     * @param value
     * @return
     */
    public long srem(String key, String value) {
        try {
            jedis = pool.getResource();
            return jedis.srem(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        }
        return 0;
    }

    /**
     * 查看集合数量
     * @param key
     * @return
     */
    public long scard(String key) {
        try {
            jedis = pool.getResource();
            return jedis.scard(key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        }
        return 0;
    }

    /**
     * 查看集合是否存在这个元素
     * @param key
     * @param value
     * @return
     */
    public boolean sismember(String key, String value) {
        try {
            jedis = pool.getResource();
            return jedis.sismember(key, value);

        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        }
        return false;
    }
}
