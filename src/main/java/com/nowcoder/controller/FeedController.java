package com.nowcoder.controller;

import com.nowcoder.model.EntityType;
import com.nowcoder.model.Feed;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.User;
import com.nowcoder.service.FeedService;
import com.nowcoder.service.FollowService;
import com.nowcoder.service.UserService;
import com.nowcoder.utils.JedisAdapter;
import com.nowcoder.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ALTERUI on 2018/12/21 21:34
 */
@Controller
public class FeedController {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private FeedService feedService;

    @Autowired
    private FollowService followService;

    @Autowired
    private JedisAdapter jedisAdapter;






    @RequestMapping(path = "/pushfeeds",method = RequestMethod.GET)

    public String getFeedUsePush(Model model) {

        int localId = hostHolder.getUser() == null ? 0 : hostHolder.getUser().getId();
        List<String> feedIds = new ArrayList<>();
        feedIds = jedisAdapter.lrange(RedisKeyUtil.getTimelineKey(localId), 0, 100);
        List<Feed> feeds = new ArrayList<>();
        for (String feedId : feedIds) {
            Feed feed = feedService.selectFeedById(Integer.parseInt(feedId));
            if (feed == null) {
                continue;
            }
            feeds.add(feed);

        }
        model.addAttribute("feeds", feeds);
        return "feeds";

    }

    @RequestMapping(path = "/pullfeeds",method = RequestMethod.GET)

    public String getFeedUsePull(Model model) {
        List<Integer> followees = new ArrayList<>();
        int localId = hostHolder.getUser() == null ? 0 : hostHolder.getUser().getId();
        if (localId != 0) {

            followees = followService.getFolloweesList(EntityType.ENTITY_USER, localId, Integer.MAX_VALUE);
        }

        List<Feed> feeds = feedService.selectFeedByIds(Integer.MAX_VALUE, followees, 100);
        model.addAttribute("feeds", feeds);
        return "feeds";


    }
}
