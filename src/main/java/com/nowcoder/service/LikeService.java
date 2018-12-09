package com.nowcoder.service;

import com.nowcoder.utils.JedisAdapter;
import com.nowcoder.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by ALTERUI on 2018/12/9 16:22
 * 1.得到赞数量
 * 2.得到踩赞的状态
 * 3.点赞发生变化(总数目赞+1，踩—1，即添加一个元素)
 * 4.点踩发生变化(总数目赞-1，踩+1，即移除一个元素)
 *
 */
@Service
public class LikeService {

    @Autowired
    private JedisAdapter jedisAdapter;


    /**
     * 得到赞的数量
     *
     * @param entityType
     * @param entityId
     * @return
     */
    public long getLikeCount(int entityType, int entityId) {
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        return jedisAdapter.scard(likeKey);

    }

    /**
     * 得到踩赞的状态
     * 赞 返回1
     * 踩 返回-1
     * 其他 0
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public int getLikeStatus(int userId, int entityType, int entityId) {
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        //查看keys里面是否有key，有的话则返回1.
        if (jedisAdapter.sismember(likeKey, String.valueOf(userId))) {
            return 1;
        }

        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        return jedisAdapter.sismember(disLikeKey, String.valueOf(userId)) ? -1 : 0;
    }

    /**
     * 表明点击赞，则移除踩
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public long like(int userId, int entityType, int entityId) {
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        jedisAdapter.sadd(likeKey, String.valueOf(userId));

        //移除踩
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        jedisAdapter.srem(disLikeKey, String.valueOf(userId));

        //返回赞的个数
        return jedisAdapter.scard(likeKey);
    }


    /**
     * 表明点击踩
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public long disLike(int userId, int entityType, int entityId) {
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        jedisAdapter.srem(likeKey, String.valueOf(userId));
        jedisAdapter.sadd(disLikeKey, String.valueOf(userId));
        return jedisAdapter.scard(likeKey);


    }






}
