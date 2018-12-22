package com.nowcoder.async.handle;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.Feed;
import com.nowcoder.model.Question;
import com.nowcoder.model.User;
import com.nowcoder.service.FeedService;
import com.nowcoder.service.FollowService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.UserService;
import com.nowcoder.utils.JedisAdapter;
import com.nowcoder.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * Created by ALTERUI on 2018/12/21 20:22
 */
@Component
public class FeedHandle implements EventHandler {
    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private FeedService feedService;

    @Autowired
    private FollowService followService;

    @Autowired
    private JedisAdapter jedisAdapter;


    /**
     * 构建data
     * @param model
     * @return
     */
    private String buildDataJson(EventModel model) {
        Map<String, String> map = new HashMap<>();
        User user = userService.getUser(model.getActorId());
        if (user == null) {
            return null;
        }
        map.put("userId", String.valueOf(user.getId()));
        map.put("userName", user.getName());
        map.put("userHead", user.getHeadUrl());


        //问题
        if (model.getEventType() == EventType.COMMIT
                || (model.getEventType() == EventType.FOLLOW && (model.getEntityType()
                == EntityType.ENTITY_QUESTION))) {

            Question question = questionService.selectQuestionById(model.getEntityId());

            if (question == null) {
                return null;
            }
            if (question.getTitle() == null) {
                return null;
            }
            map.put("questionTitle", question.getTitle());
            map.put("questionId", String.valueOf(question.getId()));


            return JSONObject.toJSONString(map);
        }
        return null;

    }

    @Override
    public void doHandle(EventModel model) {


        Feed feed = new Feed();
        feed.setCreatedDate(new Date());
        feed.setType(model.getEventType().getTypeValue());
        feed.setUserId(model.getActorId());
        if (buildDataJson(model) == null) {
            return;
        }
        feed.setData(buildDataJson(model));



        feedService.addFeed(feed);


        //给事件的粉丝推
        List<Integer> followers = new ArrayList<>();
        followers = followService.getFollowersList(EntityType.ENTITY_USER, model.getActorId(), Integer.MAX_VALUE);
        //系统
        followers.add(0);
        for (int follower : followers) {
            jedisAdapter.lpush(RedisKeyUtil.getTimelineKey(follower), String.valueOf(feed.getId()));

        }

    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(new EventType[]{EventType.FOLLOW, EventType.COMMIT});
    }

}
