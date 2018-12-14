package com.nowcoder.service;

import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;
import com.nowcoder.utils.WendaUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by ALTERUI on 2018/11/5
 */
@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDao;

    public User getUser(int id) {
        return userDAO.selectById(id);
    }

    public User getUserByName(String name) {
        return userDAO.selectByName(name);
    }

    /**
     * 将前端页面传来的username和password传入到数据库中，如果输入错误，则把错误信息反馈到前端
     * @param username
     * @param password
     * @return
     */
    public Map<String, String> register(String username, String password) {
        Map<String, String> map = new HashMap<>();
        Random random = new Random();
        if (StringUtils.isBlank(username)) {
            map.put("msg", "用户名不能为空");
            return map;
        }

        if (StringUtils.isBlank(password)) {
            map.put("msg", "密码不能不空");
            return map;
        }

        if (userDAO.selectByName(username) != null) {
            map.put("msg", "用户名已经存在，请重新输入！");
            return map;
        }

        User user = new User();
        user.setName(username);
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
        user.setSalt(UUID.randomUUID().toString().substring(0, 5));
        user.setPassword(WendaUtil.getMD5(password + user.getSalt()) );

        userDAO.addUser(user);

        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        return map;
    }

    public Map<String, String> login(String username, String password) {
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isBlank(username)) {
            map.put("msg", " 请输入用户名");
            return map;
        }

        if (StringUtils.isBlank(password)) {
            map.put("msg", "请输入密码");
            return map;
        }

        User user = userDAO.selectByName(username);

        if (user == null) {
            map.put("msg", "用户名不存在");
            return map;
        }

        if (!WendaUtil.getMD5(password + user.getSalt()).equals(user.getPassword())) {
            map.put("msg", "密码输入错误");

            return map;
        }

        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        map.put("userId", String.valueOf(user.getId()));

        return map;

    }

    private String addLoginTicket(int userId) {
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        ticket.setStatus(0);
        Date now = new Date();
        now.setTime(now.getTime() + 3600 * 24 * 100);
        ticket.setExpired(now);
        ticket.setTicket(UUID.randomUUID().toString().replace("-", ""));

        loginTicketDao.addTicket(ticket);
        return ticket.getTicket();
    }

    public void logout(String ticket) {
        loginTicketDao.updateStatus(ticket, 1);
    }


}
