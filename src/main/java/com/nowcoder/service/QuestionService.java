package com.nowcoder.service;

import com.nowcoder.dao.QuestionDAO;
import com.nowcoder.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * Created by ALTERUI on 2018/11/5
 */
@Service
public class QuestionService {
    @Autowired
    private QuestionDAO questionDAO;

    @Autowired
    private SensitiveService sensitiveService;

    public List<Question> getLatestQuestions(int userId) {
        return questionDAO.selectLatestQuestions(userId);
    }

    public int addQuestion(Question question) {
        //进行html过滤
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));

        //需要进行敏感词过滤
        question.setTitle(sensitiveService.filter(question.getTitle()));
        question.setContent(sensitiveService.filter(question.getContent()));

        return questionDAO.addQuestion(question) > 0 ? question.getUserId() : 0;
    }


    public Question selectQuestionById(int id) {
        return questionDAO.selectQuestionById(id);
    }

    public int updateCommentCounts(int id, int commentCount) {
        return questionDAO.updateCommentCount(id, commentCount);
    }
}
