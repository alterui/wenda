package com.nowcoder.controller;

import com.nowcoder.service.UserService;
import com.sun.deploy.net.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by ALTERUI on 2018/11/5 13:34
 */
@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping(path = "/reg", method = RequestMethod.POST)
    public String register(Model model,
                           @RequestParam("username") String username,
                           @RequestParam("password") String password,
                           HttpServletResponse response) {

        /**
         * 对于service数据进行异常处理
         */
        try {
            Map<String, String> map = userService.register(username, password);
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket"));
                cookie.setPath("/");
                response.addCookie(cookie);
                return "redirect:/";
            } else {
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }
        } catch (Exception e) {
            logger.error("注册异常" + e.getMessage());
            return "/login";
        }
    }

    @RequestMapping(path = "/regLogin" ,method = RequestMethod.GET)
    public String reg() {
        return "login";
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(Model model,
                        @RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam(value = "rememberme", defaultValue = "false") boolean rememberme,
                        HttpServletResponse response) {

        Map<String, String> mapLogin = userService.login(username, password);
        try {
            if (mapLogin.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", mapLogin.get("ticket"));
                cookie.setPath("/");
                response.addCookie(cookie);
                return "redirect:/";
            } else {
                model.addAttribute("msg", mapLogin.get("msg"));
                return "login";
            }

        } catch (Exception e) {
            logger.error("登录异常" + e.getMessage());
            return "login";
        }

    }




}
