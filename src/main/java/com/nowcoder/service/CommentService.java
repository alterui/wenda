package com.nowcoder.service;

import com.nowcoder.dao.CommentDAO;
import com.nowcoder.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * Created by ALTERUI on 2018/11/29 15:14
 */
@Service
public class CommentService {
    @Autowired
    private CommentDAO commentDAO;

    @Autowired
    private SensitiveService sensitiveService;

    /**
     * 增加一条评论
     * @param comment
     * @return
     */
    public int addComment(Comment comment) {
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveService.filter(comment.getContent()));

        return commentDAO.addComment(comment);
    }

    /**
     * 通过entityId和entityType
     * 通过实体获取所有评论
     * @param entityId
     * @param entityType
     * @param offset
     * @param limit
     * @return
     */
    public List<Comment> getCommentListByEntity(int entityId, int entityType,int offset,int limit) {
        return commentDAO.selectCommentsByEntity(entityId, entityType, offset, limit);
    }

    /**
     * 获取评论的数量
     * @param entityId
     * @param entityType
     * @return
     */
    public int getCommentCounts(int entityId, int entityType) {
        return commentDAO.getCommentCounntByEntity(entityId, entityType);
    }


    public boolean deleteComment(int commentId) {
        return commentDAO.updateCommentStatus(commentId, 1) > 0 ? true : false;

    }

    public Comment getCommentById(int id) {
        return commentDAO.getCommentById(id);


    }

}
