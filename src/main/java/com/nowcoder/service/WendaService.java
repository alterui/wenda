package com.nowcoder.service;

import org.springframework.stereotype.Service;

/**
 * Created by ALTERUI on 2018/11/5
 */
@Service
public class WendaService {
    public String getMessage(int userId) {
        return "Hello Message:" + String.valueOf(userId);
    }
}
