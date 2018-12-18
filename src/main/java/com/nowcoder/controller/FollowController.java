package com.nowcoder.controller;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Question;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.FollowService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.UserService;
import com.nowcoder.utils.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ALTERUI on 2018/12/17 15:28
 */
@Controller
public class FollowController {
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private QuestionService questionService;


    @Autowired
    private FollowService followService;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    /**
     * 关注一个用户
     * @param userId
     * @return
     */
    @RequestMapping(path = "/followUser", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String followUser(@RequestParam("userId") int userId) {
        if (hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }

        boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);

        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(hostHolder.getUser().getId())
                .setEntityType(EntityType.ENTITY_USER)
                .setEntityId(userId)
                .setEntityOwnerId(userId));
        //当前用户的关注对象的数目。q:不知道该值在前端哪里使用
        return WendaUtil.getJSONString(ret ? 0 : 1, String.valueOf(followService.getFolloweeCount(EntityType.ENTITY_USER, hostHolder.getUser().getId())));
    }


    /**
     * 取消关注一个人
     * @param userId
     * @return
     */
    @RequestMapping(path = "/unfollowUser", method = RequestMethod.POST)
    @ResponseBody
    public String unfollowUser(@RequestParam("userId") int userId) {
        if (hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }

        boolean ret = followService.unFollow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);


        //当前用户的关注对象的数目。q:不知道该值在前端哪里使用
        return WendaUtil.getJSONString(ret ? 0 : 1, String.valueOf(followService.getFolloweeCount(EntityType.ENTITY_USER, hostHolder.getUser().getId())));
    }


    /**
     * 关注一个问题
     * @param questionId
     * @return
     */
    @RequestMapping(path = "/followQuestion", method = RequestMethod.POST)
    @ResponseBody
    public String followQuestion(@RequestParam("questionId") int questionId) {
        if (hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }

        Question question = questionService.selectQuestionById(questionId);
        if (question == null) {
            return WendaUtil.getJSONString(1, "该问题不存在");
        }

        boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);

        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(hostHolder.getUser().getId())
                .setEntityType(EntityType.ENTITY_QUESTION)
                .setEntityId(questionId)
                .setEntityOwnerId(question.getId()));//交互对象的id，即为该id值为一个用户，即触发者触发的id；


        //需要把问题关注者的头像，姓名，和id传到前端，还有该问题关注的总人数。
        Map<String, Object> info = new HashMap<>();
        info.put("headUrl", hostHolder.getUser().getHeadUrl());
        info.put("name", hostHolder.getUser().getName());
        info.put("id", hostHolder.getUser().getId());
        info.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));


        return WendaUtil.getJSONString(ret ? 0 : 1, info);
    }


    @RequestMapping(path = "/unfollowQuestion", method = RequestMethod.POST)
    @ResponseBody
    public String unfollowQuestion(@RequestParam("questionId") int questionId) {
        if (hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }

        Question question = questionService.selectQuestionById(questionId);
        if (question == null) {
            return WendaUtil.getJSONString(1, "该问题不存在");
        }

        boolean ret = followService.unFollow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);



        //需要把问题关注者的头像，姓名，和id传到前端，还有该问题关注的总人数。
        Map<String, Object> info = new HashMap<>();
        info.put("headUrl", hostHolder.getUser().getHeadUrl());
        info.put("name", hostHolder.getUser().getName());
        info.put("id", hostHolder.getUser().getId());
        info.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));


        return WendaUtil.getJSONString(ret ? 0 : 1, info);
    }


    /**
     * 当前用户的关注人列表
     * @param model
     * @param userId
     * @return
     */
    @RequestMapping(path = {"/user/{uid}/followees"}, method = {RequestMethod.GET})
    public String followees(Model model, @PathVariable("uid") int userId) {
        List<Integer> followeesList = followService.getFolloweesList(EntityType.ENTITY_USER, userId, 0, 10);
        if (hostHolder.getUser() != null) {
            model.addAttribute("followees", getUsersInfo(hostHolder.getUser().getId(), followeesList));
        } else {
            model.addAttribute("followees", getUsersInfo(0, followeesList));
        }

        model.addAttribute("followeeCount", followService.getFolloweeCount(userId,EntityType.ENTITY_USER));
        model.addAttribute("curUser", userService.getUser(userId));
        return "followees";
    }

    /**
     * 当前用户的粉丝列表
     * @param model
     * @param userId
     * @return
     */
    @RequestMapping(path = {"/user/{uid}/followers"}, method = {RequestMethod.GET})
    public String followers(Model model, @PathVariable("uid") int userId) {
        List<Integer> followersList = followService.getFollowersList(EntityType.ENTITY_USER, userId, 0, 10);
        if (hostHolder.getUser() != null) {
            model.addAttribute("followers", getUsersInfo(hostHolder.getUser().getId(), followersList));
        } else {
            model.addAttribute("followers", getUsersInfo(0, followersList));
        }

        model.addAttribute("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER,userId));
        model.addAttribute("curUser", userService.getUser(userId));
        return "followers";
    }

    /**
     * 将用户，评论数，粉丝数，是否关注绑定在vo里
     * @param localId
     * @param userIds
     * @return
     */
    public List<ViewObject> getUsersInfo(int localId,List<Integer> userIds) {
        List<ViewObject> usersInfos = new ArrayList<>();

        for (int id : userIds) {
            ViewObject vo = new ViewObject();
            if (userService.getUser(id) == null) {
                continue;
            }
            vo.set("user", userService.getUser(id));
            vo.set("commentCount", commentService.getCommentCountById(id));
            vo.set("followeeCount", followService.getFolloweeCount(EntityType.ENTITY_USER, id));
            vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, id));

            if (localId != 0) {
                vo.set("followed", followService.isFollower(localId, EntityType.ENTITY_USER, id));
            } else {
                vo.set("followed", false);
            }

            usersInfos.add(vo);
        }
        return usersInfos;

    }

}
