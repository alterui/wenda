package com.nowcoder.controller;

import com.nowcoder.model.*;
import com.nowcoder.service.CommentService;

import com.nowcoder.service.FollowService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ALTERUI on 2018/11/4
 */

@Controller
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserService userService;

    @Autowired
    private FollowService followService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;


    @RequestMapping(path = {"/user/{userId}"}, method = RequestMethod.GET)
    public String userHome(Model model,
                           @PathVariable("userId") int userId,
                           HttpServletResponse response) {
        model.addAttribute("vos", getQuestion(userId, 0, 10));
        User user = userService.getUser(userId);
        ViewObject vo = new ViewObject();

        vo.set("user", user);
        vo.set("commentCount", commentService.getCommentCountById(userId));
        vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        vo.set("followeeCount", followService.getFolloweeCount(userId,EntityType.ENTITY_USER ));
        if (hostHolder.getUser() != null) {
            vo.set("followed", followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId));
        } else {
            vo.set("followed", false);
        }
        model.addAttribute("profileUser", vo);
        return "profile";

    }

    @RequestMapping(path = {"/", "/index"}, method = RequestMethod.GET)
    public String home(Model model) {


        model.addAttribute("localUser", hostHolder.getUser());


        model.addAttribute("vos", getQuestion(0,0,10));
        return "index";
    }

    private List<ViewObject> getQuestion(int userId, int offset, int limit) {

        /**
         * 前端问题广场页面显示的是问题+用户，所以可以返回问题+用户。
         * 做法：可以创建一个vo类，用于存放问题+用户
         */
        List<ViewObject> vos = new ArrayList<>();
        //把问题表的数据读取过来
        List<Question> questionsList = questionService.getLatestQuestions(userId);
        for (Question question : questionsList) {
            //把问题和问题提出的用户绑定起来
            ViewObject vo = new ViewObject();
            vo.set("question", question);
            vo.set("user", userService.getUser(question.getUserId()));
            //将用户粉丝传入
            vo.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));


            vos.add(vo);
        }
        return vos;
    }



}
