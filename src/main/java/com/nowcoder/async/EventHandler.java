package com.nowcoder.async;

import java.util.List;

/**
 * Created by ALTERUI on 2018/12/11 13:54
 */
public interface EventHandler {

    /**
     * 处理事件
     * @param model
     */
    void doHandle(EventModel model);

    /**
     * 关注的Event
     * @return
     */
    List<EventType> getSupportEventTypes();
}
