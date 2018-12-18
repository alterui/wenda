package com.nowcoder.controller;

import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
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
        if (hostHolder.getUser() == null) {
            return "redirect:/reglogin";
        }
        int localUserId = hostHolder.getUser().getId();
        List<Message> conversationList = messageService.getConversationList(localUserId, 0, 10);

        List<ViewObject> conversations = new ArrayList<ViewObject>();

        for (Message message : conversationList) {

            ViewObject vo = new ViewObject();
            vo.set("message", message);
            int targetId = message.getFromId() == localUserId ? message.getToId() : message.getFromId();
            vo.set("user", userService.getUser(targetId));

            vo.set("unread", messageService.getUnReadCount(hostHolder.getUser().getId(), message.getConversationId()));
            conversations.add(vo);
        }
        model.addAttribute("conversations", conversations);
        return "letter";
    }

    @RequestMapping(path = "/msg/detail",method = RequestMethod.GET)
    public String getConversationDetail(@RequestParam("conversationId") String conversationId,
                                        Model model) {

        try {

            messageService.updateUnReadCount(hostHolder.getUser().getId(), conversationId);
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


    /**
     * 读过私信之后，将未读数目置为0
     * 缺点：没有使用ajax动态刷新。
     * 可以直接在questionController中完成这个功能
     * @param conversationId
     * @return
     */
    /*@RequestMapping(path = "/msg/remHasRead/{conversationId}", method = RequestMethod.GET)
    @ResponseBody
    public String remHasRead(@PathVariable("conversationId") String conversationId) {

        if (hostHolder.getUser() == null) {
            return "redirect:/";
        }
        messageService.updateUnReadCount(hostHolder.getUser().getId(), conversationId);

        return WendaUtil.getJSONString(0);

    }*/
}
