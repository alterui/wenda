package com.nowcoder.controller;

import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import com.nowcoder.utils.WendaUtil;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ALTERUI on 2018/12/4 21:17
 */
@Controller
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @RequestMapping(path = "/msg/addMessage", method = RequestMethod.POST)
    @ResponseBody
    public String addMessage(@RequestParam("toName") String toName,
                             @RequestParam("content") String content) {
        try {
            if (hostHolder.getUser() == null) {
                WendaUtil.getJSONString(999, "用户没有登录");
            }

            Message message = new Message();
            User user = userService.getUserByName(toName);
            if (user == null) {
                WendaUtil.getJSONString(1, "用户不存在");
            }
            message.setToId(user.getId());
            message.setFromId(hostHolder.getUser().getId());
            message.setContent(content);
            message.setCreatedDate(new Date());
            //message.setHasRead(0);

            messageService.addMessage(message);
            return WendaUtil.getJSONString(0);


        } catch (Exception e) {
            logger.error("发送消息失败" + e.getMessage());
            return WendaUtil.getJSONString(1, "发送消息失败");
        }


    }


    @RequestMapping(path = "/msg/list", method = RequestMethod.GET)
    public String getConversationList(Model model) {
        try {
            if (hostHolder.getUser() == null) {
                return "redirect:/";
            }
            List<Message> conversationList = messageService.getConversationList(hostHolder.getUser().getId(), 0, 10);
            List<ViewObject> messages = new ArrayList<>();
            for (Message message : conversationList) {
                ViewObject vo = new ViewObject();
                vo.set("conversation", message);
                //读取发送者的信息，即发送方
                vo.set("user", userService.getUser(message.getFromId()));
                //未读个数
                vo.set("unread", messageService.getUnReadCount(hostHolder.getUser().getId(), message.getConversationId()));
                //将消息和对方用户绑定到vo中
                messages.add(vo);
            }

            model.addAttribute("conversations", messages);
        } catch (Exception e) {
            logger.error("获取会话列表失败" + e.getMessage());
        }
        return "letter";

    }

    @RequestMapping(path = "/msg/detail",method = RequestMethod.GET)
    public String getConversationDetail(@RequestParam("conversationId") String conversationId,
                                        Model model) {

        try {
            List<Message> messagesList = messageService.getMessagesByConversationId(conversationId, 0, 10);
            List<ViewObject> messages = new ArrayList<>();

            for (Message message : messagesList) {
                ViewObject vo = new ViewObject();
                vo.set("message", message);
                User user = userService.getUser(message.getFromId());
                vo.set("user", user);

                messages.add(vo);
            }

            model.addAttribute("messages", messages);


        } catch (Exception e) {
            logger.error("获取详情失败"+e.getMessage());
        }
        return "letterDetail";
    }
}
