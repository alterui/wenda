package com.nowcoder.service;

import com.nowcoder.dao.FeedDAO;
import com.nowcoder.model.Feed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ALTERUI on 2018/12/21 19:40
 */
@Service
public class FeedService {
    @Autowired
    private FeedDAO feedDAO;

    public List<Feed> selectFeedByIds(int maxId, List<Integer> userIds, int count) {
        return feedDAO.selectFeedByIds(maxId, userIds, count);
    }

    public boolean addFeed(Feed feed) {
        feedDAO.addFeed(feed);
        return feed.getId() > 0;
    }

    public Feed selectFeedById(int id) {
        return feedDAO.selectFeedById(id);
    }


}
