package com.nowcoder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nowcoder.model.User;
import redis.clients.jedis.Jedis;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

/**
 * Created by ALTERUI on 2018/12/7 9:32
 */
public class JedisAdapterTest {
    public static void print(int index, Object object) {
        System.out.println(String.format("%d,%s", index, object.toString()));
    }

    public static void main(String[] args) {
        Jedis jedis = new Jedis("redis://localhost:6379/9");
        //将连接的数据库的值全部删除
        jedis.flushDB();


    }

    public static void test() {


        Jedis jedis = new Jedis("redis://localhost:6379/9");
        //将连接的数据库的值全部删除
        jedis.flushDB();
        /**
         * set - get
         */
        //设置key-value
        jedis.set("hello", "world");

        print(1, jedis.get("hello"));

        //修改key的名字
        jedis.rename("hello", "newHello");

        //到一定时间，自动删除
        jedis.setex("hello2", 10, "world");


        /**
         * 数字变化，增加减少
         */
        jedis.set("pv", "100");
        //自增1
        jedis.incr("pv");
        print(2, jedis.get("pv"));

        //增加指定数字
        jedis.incrBy("pv", 9);
        print(2,jedis.get("pv"));

        //减少指定数字
        jedis.decrBy("pv", 5);
        print(2, jedis.get("pv"));

        //自减1
        jedis.decr("pv");
        print(1, jedis.get("pv"));

        /**
         * list
         */
        String list = "list";
        /**
         * 数据结构是先进后出，和stack一样，stack
         */
        for (int i = 0; i < 10; i++) {
            jedis.lpush("list", "a"+String.valueOf(i));
        }

        print(3,jedis.lrange(list,0,9));//a9, a8, a7, a6, a5, a4, a3, a2, a1, a0
        print(3, jedis.lrange(list, 0, 2));//a9, a8, a7
        //获取list长度
        print(4, jedis.llen(list));
        //弹出一个元素
        print(5,jedis.lpop(list));
        //直接取元素
        print(6, jedis.lindex(list, 0));//a8

        //动态插入元素
        //print(7, jedis.linsert(list, ListPosition.AFTER, "a4", "xx"));
        //print(8, jedis.linsert(list, ListPosition.BEFORE, "a4", "bb"));
        print(9,jedis.lrange(list,0,12));


        /**
         * hash
         */
        String userKey = "userXXX";

        //set值
        jedis.hset(userKey, "name", "tom");
        jedis.hset(userKey, "age", "23");
        jedis.hset(userKey, "phone", "1586555555");

        //get值
        print(10, jedis.hget(userKey, "name"));
        //key-value形式
        print(11, jedis.hgetAll(userKey));

        //删除一个key
        jedis.hdel(userKey, "phone");
        print(12, jedis.hgetAll(userKey));

        //是否存在
        print(13, jedis.hexists(userKey, "name"));
        print(14, jedis.hexists(userKey, "phone"));

        //获取key
        print(15, jedis.hkeys(userKey));
        print(16, jedis.hvals(userKey));

        //set不存在的zhi
        jedis.hsetnx(userKey, "name", "lr");//存在key->name,所以name值仍然为tom
        jedis.hsetnx(userKey, "school", "njut");
        print(17, jedis.hgetAll(userKey));


        /**
         * set 集合
         */

        String likeKey1 = "commentLike1";
        String likeKey2 = "commentLike2";
        //给集合注入值
        for (int i = 0; i < 10; i++) {
            jedis.sadd(likeKey1, String.valueOf(i));
            jedis.sadd(likeKey2, String.valueOf(i * i));
        }

        //取集合的值
        print(18, jedis.smembers(likeKey1));
        print(19, jedis.smembers(likeKey2));

        //两集合的并集
        print(20, jedis.sunion(likeKey1, likeKey2));

        //我有你没有？
        print(21, jedis.sdiff(likeKey1, likeKey2));
        print(21, jedis.sdiff(likeKey2, likeKey1));

        //两个集合的交集
        print(22, jedis.sinter(likeKey1, likeKey2));

        //是否有这个成员
        print(23, jedis.sismember(likeKey1, "10"));
        print(23, jedis.sismember(likeKey2, "16"));

        //删除一个元素
        print(24, jedis.srem(likeKey1, "0"));

        //把likeKey2中的15移动到likeKey1z
        jedis.smove(likeKey2, likeKey1, "16");
        print(24,jedis.smembers(likeKey1));
        print(24, jedis.smembers(likeKey2));


        print(25, jedis.scard(likeKey1));


        /**
         * 排行榜
         *
         */

        String rankKey = "rankKey";
        jedis.zadd(rankKey, 68, "tom");
        jedis.zadd(rankKey, 89, "jim");
        jedis.zadd(rankKey, 93, "lee");
        jedis.zadd(rankKey, 96, "liu");

        //优先队列的数目
        print(26, jedis.zcard(rankKey));

        //一个范围的数量
        print(27, jedis.zcount(rankKey, 80, 90));

        //打印一个用户的分数
        print(28, jedis.zscore(rankKey, "liu"));

        //增加几分

        jedis.zincrby(rankKey, 2, "liu");
        print(29, jedis.zscore(rankKey, "liu"));
        jedis.zincrby(rankKey, 69, "rui");
        print(30, jedis.zscore(rankKey, "rui"));
        print(31, jedis.zrange(rankKey, 0, 100));//从小往大排序
        print(32, jedis.zrange(rankKey, 0, 2));
        print(33, jedis.zrevrange(rankKey, 0, 1));
        print(34, jedis.zrevrange(rankKey, 0, 0));

        //遍历一个范围的用户，然后将用户和用户名打印出来
        for (Tuple tuple : jedis.zrangeByScoreWithScores(rankKey, 60, 100)) {
            print(35, tuple.getElement() + ":" + tuple.getScore());
        }

        //查看一个用户的排名
        print(36, jedis.zrank(rankKey, "liu"));
        print(36, jedis.zrevrank(rankKey, "liu"));

        String setKey = "zset";
        jedis.zadd(setKey,1, "a");
        jedis.zadd(setKey,1, "b");
        jedis.zadd(setKey,1, "c");
        jedis.zadd(setKey,1, "d");
        jedis.zadd(setKey,1, "e");
        jedis.zadd(setKey, 1, "f");


        //一定范围的数量
        print(37, jedis.zlexcount(setKey, "-", "+"));
        print(38, jedis.zlexcount(setKey, "[b", "[d"));
        print(38, jedis.zlexcount(setKey, "(b", "[d"));

        //移除一个
        print(39, jedis.zrem(setKey, "b"));

        //移除一定范围的
        jedis.zremrangeByLex(setKey, "(c", "+");
        print(40, jedis.zrange(setKey, 0, 4));

        /**
         * 连接池
         */
        print(41, jedis.get("pv"));


        JedisPool pool = new JedisPool();
        for (int i = 0; i < 100; i++) {
            Jedis j =  pool.getResource();
            j.set("pv1", "100");
            System.out.println(j.get("pv1"));
            print(42, j.get("pv1"));
            j.close();//默认是8条线程
        }

        User user = new User();
        user.setPassword("*****");
        user.setId(1);
        user.setHeadUrl("a.jpg");
        user.setName("liu");
        user.setSalt("salt");

        //将对象转换成json
        jedis.set("user", JSONObject.toJSONString(user));

        print(43, JSONObject.toJSONString(user));

        //将json转换为对象
        String user1 = jedis.get("user");
        User user2 = JSON.parseObject(user1, User.class);
        print(44,user2);


    }

}
