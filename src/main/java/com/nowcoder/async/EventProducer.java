package com.nowcoder.async;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.utils.JedisAdapter;
import com.nowcoder.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 事件生产者，即把事件推向队列
 * Created by ALTERUI on 2018/12/11 13:18
 */
@Service
public class EventProducer {
    @Autowired
    private JedisAdapter jedisAdapter;

    public boolean fireEvent(EventModel eventModel) {

        try {
            //将eventModel对象序列化
            String jsonString = JSONObject.toJSONString(eventModel);
            String key = RedisKeyUtil.getEventQueue();
            //将事件放入优先队列中
            jedisAdapter.lpush(key,jsonString);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
