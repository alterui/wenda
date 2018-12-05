package com.nowcoder.service;



import com.nowcoder.dao.MessageDAO;
import com.nowcoder.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by ALTERUI on 2018/12/4 21:04
 */
@Service
public class MessageService {

    @Autowired
    private MessageDAO messageDAO;

    @Autowired
    private SensitiveService sensitiveService;

    /**
     * 增加一个消息
     * @param message
     * @return
     */
    public int addMessage(Message message) {
        message.setContent(sensitiveService.filter(message.getContent()));
        return messageDAO.addMessage(message);
    }

    /**
     * 通过会话id获取会话详情
     * @param conversationId
     * @param offset
     * @param limit
     * @return
     */
    public List<Message> getMessagesByConversationId(String conversationId,int offset,int limit) {
        return messageDAO.selectMessagesByConversationId(conversationId, offset, limit);
    }

    /**
     * 获取当前用户的所有会话列表
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    public List<Message> getConversationList(int userId, int offset, int limit) {
        return messageDAO.selectConversationList(userId, offset, limit);
    }

    public int getUnReadCount(int userId, String conversationId) {
        return messageDAO.getUnReadCount(userId, conversationId);
    }

}
