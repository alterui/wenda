package com.nowcoder.utils;

/**
 * Created by ALTERUI on 2018/12/9 19:15
 * 为了redis的key不重复
 */
public class RedisKeyUtil {
    private static String SPLIT = ":";
    private static String BIZ_LIKE = "LIKE";
    private static String BIZ_DISLIKE = "DISLIKE";
    //事件队列
    private static String BIZ_EVENTQUEUE = "EVENT_QUEUE";
    //获取粉丝
    private static String BIZ_FOLLOWER = "FOLLOWER";
    //关注对象
    private static String BIZ_FOLLOWEE = "FOLLOWEE";

    private static String BIZ_TIMELINE = "TIMELINE";

    /**
     * 获取LikeKey,"dd"+1,会直接变成字符串
     * @param entityType
     * @param entityId
     * @return
     */
    public static String getLikeKey(int entityType, int entityId) {
        return BIZ_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    public static String getDisLikeKey(int entityType, int entityId) {
        return BIZ_DISLIKE + SPLIT + entityType + SPLIT + entityId;
    }

    public static String getEventQueue() {
        return BIZ_EVENTQUEUE;
    }

    /**
     * 每个关注对象即实体所拥有粉丝的key
     * @param entityType
     * @param entityId
     * @return
     */
    public static String getFollowerKey(int entityType, int entityId) {
        return BIZ_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 每个用户关注的对象
     * @param userId
     * @param
     * @return
     */
    public static String getFolloweeKey(int userId, int entityType) {
        return BIZ_FOLLOWEE + SPLIT + String.valueOf(userId) + SPLIT + String.valueOf(entityType);
    }

    public static String getTimelineKey(int userId) {
        return BIZ_TIMELINE + SPLIT + userId;
    }



}
