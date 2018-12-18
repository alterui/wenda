package com.nowcoder.async;

import com.alibaba.fastjson.JSON;
import com.nowcoder.utils.JedisAdapter;
import com.nowcoder.utils.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 事件的消费者
 * Created by ALTERUI on 2018/12/11 14:01
 */
@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private JedisAdapter jedisAdapter;
    //一个EventType对应很多个EventHandler
    private Map<EventType, List<EventHandler>> config = new HashMap<>();
    //上下文
    private ApplicationContext applicationContext;


    @Override
    public void afterPropertiesSet() throws Exception {
        //把实现EventHandler接口的类全部加载进来
        Map<String, EventHandler> beansOfType = applicationContext.getBeansOfType(EventHandler.class);
        if (beansOfType != null) {
            //遍历实现EventHandle接口类的键
            for (Map.Entry<String, EventHandler> entry : beansOfType.entrySet()) {
                List<EventType> supportEventTypes = entry.getValue().getSupportEventTypes();
                //注册事件
                for (EventType eventType : supportEventTypes) {
                    if (!config.containsKey(eventType)) {
                        config.put(eventType, new ArrayList<>());
                    }
                    //添加
                    config.get(eventType).add(entry.getValue());



                }
            }
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String key = RedisKeyUtil.getEventQueue();
                    //事件从右侧出来，先进先出
                    List<String> events = jedisAdapter.brpop(0, key);

                    //过滤key
                    for (String message : events) {
                        if (message.equals(key)) {
                            continue;
                        }

                        //反序列化,将event反序列化为EventModel
                        EventModel eventModel = JSON.parseObject(message, EventModel.class);

                      /*  if (!config.containsKey(eventModel.getEventType())) {
                            logger.error("不能识别的事件");

                        }*/

                        for (EventHandler handler : config.get(eventModel.getEventType())) {

                            handler.doHandle(eventModel);
                        }
                    }
                }
            }
        });

        thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
