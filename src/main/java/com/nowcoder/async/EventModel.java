package com.nowcoder.async;

import java.util.HashMap;
import java.util.Map;

/**
 * 事件模型
 * Created by ALTERUI on 2018/12/10 21:54
 */
public class EventModel {
    private EventType eventType;//表示事件模型
    private int actorId; //触发事件者Id
    private int entityType;//该事件模型的发生对象类型
    private int entityId;//该事件模型的发生对象id,例如发生的对象类型为Message，则entityId则为message的entityId
    private int entityOwnerId; //交互对象的id
    private Map<String,String> exts = new HashMap<>();//拓展字段

    public EventModel() {

    }

    public EventModel(EventType type) {
        this.eventType = type;

    }

    /**
     * 可能拓展字段只有一个字段，不需要用集合
     * 方便存取
     * @param key
     * @param value
     * @return setter()返回类型改写为EventModel，可以链式调用，即xx.setEventType().setActorId().setXx()...
     */
    public EventModel setExt(String key, String value) {
        exts.put(key, value);
        return this;
    }

    public String getExt(String key) {
        return exts.get(key);
    }

    public EventType getEventType() {
        return eventType;
    }

    public EventModel setEventType(EventType eventType) {
        this.eventType = eventType;
        return this;
    }

    public int getActorId() {
        return actorId;
    }

    public EventModel setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    public EventModel setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }

    public Map<String,String> getExts() {
        return exts;
    }

    public EventModel setExts(Map<String,String> exts) {
        this.exts = exts;
        return this;
    }
}
