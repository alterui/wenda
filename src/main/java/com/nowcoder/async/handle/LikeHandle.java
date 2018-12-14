package com.nowcoder.async.handle;

import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
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
 * 点赞handle
 * Created by ALTERUI on 2018/12/13 19:50
 */
@Component
public class LikeHandle implements EventHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;
    @Override
    public void doHandle(EventModel model) {
        Message message = new Message();

        //系统管理员发送消息
        message.setFromId(WendaUtil.ADMIN_USERID);

        message.setToId(model.getEntityOwnerId());
        message.setCreatedDate(new Date());

        //actorId为触发者id，即为谁点了赞，触发了这一事件
        User user = userService.getUser(model.getActorId());
        StringBuffer stringBuffer = new StringBuffer();
        /*stringBuffer.append("用户" + user.getName());
        stringBuffer.append("赞了您的评论，点击链接查看");
        stringBuffer.append("<html>");

        stringBuffer.append("<meta http-equiv='refresh' url='http://127.0.0.1:8080/question\"+ model.getExt(\"questionId\")'");
        stringBuffer.append(">");
        stringBuffer.append("http:127.0.0.1:8080/question\"+ model.getExt(\"questionId\")");
        //stringBuffer.append("</a>");
        stringBuffer.append("</html>");*/

        stringBuffer.append("<html>");
        stringBuffer.append("用户" + user.getName());
        stringBuffer.append("赞了您的评论，点击链接查看");
        stringBuffer.append("<a href='http://127.0.0.1:8080/question/" +model.getExt("questionId") +" ' target='_blank' >");
        stringBuffer.append("http:127.0.0.1:8080/question/"+model.getExt("questionId")+"");
        stringBuffer.append("</a></html>");
        message.setContent(stringBuffer.toString());

        messageService.addMessage(message);

    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
