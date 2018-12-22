package com.nowcoder.model;

import com.alibaba.fastjson.JSONObject;

import java.util.Date;

/**
 * 新鲜事
 * Created by ALTERUI on 2018/12/21 19:13
 */
public class Feed {
    private int id;
    //新鲜事的类型
    private int type;
    private int userId;
    private Date createdDate;
    //json格式，比如某人某时关注了一个问题。
    private String data;

    //让前端快速调用data里的内容
    private JSONObject dataJSON = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
        dataJSON = JSONObject.parseObject(data);
    }

    public String get(String key) {
        return dataJSON == null ? null : dataJSON.getString(key);
    }



}
