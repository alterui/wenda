package com.nowcoder.utils;

/**
 * Created by ALTERUI on 2018/12/9 19:15
 * 为了redis的key不重复
 */
public class RedisKeyUtil {
    private static String SPLIT = ":";
    private static String BIZ_LIKE = "LIKE";
    private static String BIZ_DISLIKE = "DISLIKE";

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


}
