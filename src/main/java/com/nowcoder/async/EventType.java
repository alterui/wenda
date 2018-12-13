package com.nowcoder.async;

/**
 * 表示异步的事件类型
 * 枚举类
 * Created by ALTERUI on 2018/12/10 21:47
 */
public enum EventType {
    LIKE(0),
    COMMIT(1),
    LOGIN(2),
    MAIL(3);

    private int typeValue;


    EventType(int value) {
        this.typeValue = value;
    }


    public int getTypeValue() {
        return typeValue;
    }

}


