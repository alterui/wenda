package com.nowcoder.model;

import org.springframework.stereotype.Component;

/**
 * Created by ALTERUI on 2018/11/8 19:46
 * 通过cookie获取用户，然后将用户存放到上下文
 */

@Component
public class HostHolder {
    private static ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();

    }
}
