package com.nowcoder.service;

import com.nowcoder.utils.JedisAdapter;
import com.nowcoder.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.*;

/**
 * Created by ALTERUI on 2018/12/15 15:36
 */
@Service
public class FollowService {
    @Autowired
    private JedisAdapter jedisAdapter;

    /**
     * 用户关注了一个实体，可以是关注用户或者关注问题或者关注课程。
     * 比如我关注一个问题，我是这个问题的粉丝，这个问题是我的关注对象
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public boolean follow(int userId, int entityType, int entityId) {

        //某个实体对象的粉丝key
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

        //某个用户关注的实体对象key
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Date date = new Date();

        //开启redis连接池
        Jedis jedis = jedisAdapter.getJedis();
        jedisAdapter.multi(jedis);

        //开启事务
        Transaction tx = jedisAdapter.multi(jedis);
        //把实体的粉丝添加进来
        tx.zadd(followerKey, date.getTime(), String.valueOf(userId));
        //当前用户添加该关注对象
        tx.zadd(followeeKey, date.getTime(), String.valueOf(entityType));
        //事务执行
        List<Object> exec = jedisAdapter.exec(tx, jedis);
        return exec.size() == 2 && (long) exec.get(0) > 0 && (long) exec.get(1) > 0;

    }

    /**
     * 取消关注
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public boolean unFollow(int userId, int entityType, int entityId) {

        //某个实体对象的粉丝key
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

        //某个用户关注的实体对象key
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Date date = new Date();

        //开启redis连接池
        Jedis jedis = jedisAdapter.getJedis();
        jedisAdapter.multi(jedis);

        //开启事务
        Transaction tx = jedisAdapter.multi(jedis);
        //把实体的粉丝移除
        tx.zrem(followerKey,String.valueOf(userId));
        //当前用户移除
        tx.zrem(followeeKey, String.valueOf(entityType));
        //事务执行
        List<Object> exec = jedisAdapter.exec(tx, jedis);
        return exec.size() == 2 && (long) exec.get(0) > 0 && (long) exec.get(1) > 0;

    }

    //获取粉丝列表
    public List<Integer> getFollowersList(int entityType,int entityId, int count) {
        //获取粉丝的key
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return getListFromSet(jedisAdapter.zrevrange(followerKey, 0, count));

    }

    //获取关注列表
    public List<Integer> getFolloweesList(int entityType,int userId, int count) {
        //获取粉丝的key
        String followeeKey = RedisKeyUtil.getFollowerKey(userId, entityType);
        return getListFromSet(jedisAdapter.zrevrange(followeeKey, 0, count));

    }

    /**
     * 带分页的粉丝列表
     * @param entityType
     * @param entityId
     * @param offset
     * @param count
     * @return
     */
    public List<Integer> getFollowersList(int entityType,int entityId,int offset, int count) {
        //获取粉丝的key
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return getListFromSet(jedisAdapter.zrevrange(followerKey, offset, offset + count));

    }

    /**
     * 带分页的关注列表
     * @param entityType
     * @param offset
     * @param count
     * @return
     */
    public List<Integer> getFolloweesList(int entityType,int userId,int offset, int count) {
        //获取粉丝的key
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return getListFromSet(jedisAdapter.zrevrange(followeeKey, offset, offset + count));

    }

    /**
     * 获取粉丝的个数
     * @param entityType
     * @param entityId
     * @return
     */
    public long getFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return jedisAdapter.zcard(followerKey);
    }

    /**
     * 获取关注对象的个数
     * @param entityType
     * @param userId
     * @return
     */
    public long getFolloweeCount(int entityType, int userId) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return jedisAdapter.zcard(followeeKey);
    }


    /**
     * 是否为实体对象的粉丝
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public boolean isFollower(int userId, int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        //不为null,表明是粉丝。
        return  jedisAdapter.zscore(followerKey, String.valueOf(userId)) != null;
    }





    /**
     * 将set<String>转化为List<Integer>
     * @param set
     * @return
     */
    public List<Integer> getListFromSet(Set<String> set) {
        List<Integer> list = new ArrayList<>();
        for (String ids : set) {
            list.add(Integer.parseInt(ids));
        }
        return list;
    }
}
