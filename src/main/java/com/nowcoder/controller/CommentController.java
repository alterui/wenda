package com.nowcoder.controller;

import com.nowcoder.model.Comment;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.service.CommentService;
import com.nowcoder.utils.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * Created by ALTERUI on 2018/12/4 13:02
 */
@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private HostHolder hostHolder;

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);


    @RequestMapping(path = "/addComment", method = RequestMethod.POST)
    public String addComment(@RequestParam("content") String content,
                             @RequestParam("questionId") int questionId) {

        try {
            Comment comment = new Comment();

            if (hostHolder != null) {//表明登录成功
                comment.setUserId(hostHolder.getUser().getId());

            } else {
                comment.setUserId(WendaUtil.ANONYMOUS_USERID);
            }

            comment.setCreatedDate(new Date());
            comment.setEntityId(questionId);
            comment.setEntityType(EntityType.ENTITY_QUESTION);
            comment.setContent(content);
            comment.setStatus(0);

            commentService.addComment(comment);
        } catch (Exception e) {
            logger.error("添加评论失败" + e.getMessage());
        }

        return "redirect:/question/" + questionId;

    }



}
