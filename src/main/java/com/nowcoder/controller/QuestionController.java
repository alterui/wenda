package com.nowcoder.controller;

import com.nowcoder.model.*;
import com.nowcoder.service.*;
import com.nowcoder.utils.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ALTERUI on 2018/11/23 10:02
 */
@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private FollowService followService;

    @Autowired
    private LikeService likeService;
    public static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @RequestMapping(value = "/question/add",method = RequestMethod.POST)
    @ResponseBody
    public String addQuestion(@RequestParam("title") String title,
                              @RequestParam("content")String content) {


        try {

            Question question = new Question();

            question.setTitle(title);
            question.setContent(content);
            question.setCreatedDate(new Date());
            question.setCommentCount(0);
            if (hostHolder.getUser() == null) {
                question.setUserId(999);

            } else {

                question.setUserId(hostHolder.getUser().getId());
            }
            int count = questionService.addQuestion(question);
            if (count > 0) {
                return WendaUtil.getJSONString(0);
            }
        } catch (Exception e) {
            logger.error("添加题目失败" + e.getMessage());

        }

        return WendaUtil.getJSONString(1, "添加题目失败");
    }

    @RequestMapping(value = "question/{qid}")
    public String QuestionDetail(Model model,
                                 @PathVariable("qid") int qid) {

        Question question = questionService.selectQuestionById(qid);
        User user = userService.getUser(question.getUserId());
        List<Comment> commentListByEntity = commentService.getCommentListByEntity(question.getId(), EntityType.ENTITY_QUESTION, 0, 10);
        List<ViewObject> comments = new ArrayList<>();
        for (Comment comment : commentListByEntity) {
            ViewObject vo = new ViewObject();
            vo.set("comment", comment);
            //踩赞状态
            vo.set("liked", likeService.getLikeStatus(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, comment.getId()));
            //赞的数量
            vo.set("likeCount", likeService.getLikeCount(EntityType.ENTITY_COMMENT, comment.getId()));
            //将评论者的信息绑定到vo里面
            vo.set("user", userService.getUser(comment.getUserId()));

            comments.add(vo);
        }
        model.addAttribute("comments", comments);
        model.addAttribute("question", question);
        model.addAttribute("user", user);

        List<ViewObject> followUsers = new ArrayList<>();
        // 获取关注的用户信息
        List<Integer> users = followService.getFollowersList(EntityType.ENTITY_QUESTION, qid, 20);
        for (Integer userId : users) {
            ViewObject vo = new ViewObject();
            User u = userService.getUser(userId);

            if (u == null) {
                continue;
            }
            vo.set("name", u.getName());
            vo.set("headUrl", u.getHeadUrl());
            vo.set("id", u.getId());

            followUsers.add(vo);
        }

        model.addAttribute("followUsers", followUsers);

        if (hostHolder.getUser() != null) {
            model.addAttribute("followed", followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, qid));
        } else {
            model.addAttribute("followed", false);
        }

        return "detail";
    }
}
