package com.nowcoder.controller;

import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Question;
import com.nowcoder.model.User;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.UserService;
import com.nowcoder.utils.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

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
    private HostHolder hostHolder;
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
        model.addAttribute("question", question);
        model.addAttribute("user", user);
        return "detail";
    }
}
