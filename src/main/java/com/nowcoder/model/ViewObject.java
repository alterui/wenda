package com.nowcoder.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ALTERUI on 2018/11/4
 */

public class ViewObject {
    /**
     * 可以使用Map集合存放数据
     */
    private Map<String, Object> vo = new HashMap<>();

    public void set(String key, Object value) {
        vo.put(key, value);
    }

    public Object get(String key) {
        return vo.get(key);
    }
}
