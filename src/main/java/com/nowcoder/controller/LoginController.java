package com.nowcoder.controller;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.service.UserService;
import com.sun.deploy.net.HttpResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
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

    @Autowired
    private EventProducer eventProducer;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping(path = "/reg", method = RequestMethod.POST)
    public String register(Model model,
                           @RequestParam("username") String username,
                           @RequestParam("password") String password,
                           @RequestParam(value = "next",required = false) String next,
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

                /**
                 * $!{next}：如果有值则输出，如果为空，则不显示；
                 *
                 * ${next}：如果有值则输出，如果为空，则将该代码原样输出；
                 */
                if (StringUtils.isNotBlank(next)) {
                    return "redirect:" + next;
                }

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
    public String reg(Model model,
                      @RequestParam(value = "next",required = false) String next) {
        model.addAttribute("next", next);
        return "login";
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(Model model,
                        @RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam(value = "rememberme", defaultValue = "false") boolean rememberme,
                        @RequestParam(value = "next", required = false) String next,
                        HttpServletResponse response) {

        Map<String, String> mapLogin = userService.login(username, password);
        //try {
            if (mapLogin.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", mapLogin.get("ticket"));
                cookie.setPath("/");


                if (rememberme) {
                    cookie.setMaxAge(3600 * 24 * 30);
                }
                response.addCookie(cookie);


                eventProducer.fireEvent(new EventModel(EventType.LOGIN)
                        .setExt("username", username).setExt("email", "2389889598@qq.com")
                        .setActorId(Integer.parseInt(mapLogin.get("userId"))));
                if (StringUtils.isNotBlank(next)) {
                    return "redirect:" + next;
                }
                return "redirect:/";
            } else {
                model.addAttribute("msg", mapLogin.get("msg"));
                return "login";
            }

        /*} catch (Exception e) {
            logger.error("登录异常" + e.getMessage());
            return "login";
        }*/

    }

    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue("ticket")String ticket) {

        userService.logout(ticket);

        return "redirect:/";

    }




}
