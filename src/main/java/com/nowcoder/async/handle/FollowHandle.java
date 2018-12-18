package com.nowcoder.async.handle;

import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import com.nowcoder.utils.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by ALTERUI on 2018/12/18 15:29
 */
@Component
public class FollowHandle implements EventHandler {
    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @Override
    public void doHandle(EventModel model) {
        Message message = new Message();
        message.setCreatedDate(new Date());
        message.setFromId(WendaUtil.ADMIN_USERID);
        message.setToId(model.getEntityOwnerId());//关注对象的id，例如关注人的id，关注问题的id
        message.setContent("dddduser");

        User user = userService.getUser(model.getActorId());
        StringBuffer sbUser = new StringBuffer();
        StringBuffer sbQuestion = new StringBuffer();

        //关注用户
        if (model.getEntityType()==EntityType.ENTITY_USER) {
            sbUser.append("<html>");
            sbUser.append("用户" + user.getName());
            sbUser.append("刚刚关注了您，点击链接查看");
            sbUser.append("<a href='http://127.0.0.1:8080/user/" +model.getActorId() +" ' target='_blank' >");
            sbUser.append("http:127.0.0.1:8080/user/"+model.getActorId()+"");
            sbUser.append("</a></html>");

            message.setContent(sbUser.toString());


        } else if (model.getEntityType()==EntityType.ENTITY_QUESTION) {
            sbQuestion.append("<html>");
            sbQuestion.append("用户" + user.getName());
            sbQuestion.append("刚刚关注了您的问题，点击链接查看");
            sbQuestion.append("<a href='http://127.0.0.1:8080/question/" +model.getEntityId() +" ' target='_blank' >");
            sbQuestion.append("http:127.0.0.1:8080/question/"+model.getEntityId()+"");
            sbQuestion.append("</a></html>");
            message.setContent(sbQuestion.toString());
        }

        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.FOLLOW);
    }
}
