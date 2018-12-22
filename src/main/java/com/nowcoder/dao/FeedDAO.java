package com.nowcoder.dao;

import com.nowcoder.model.Feed;
import com.nowcoder.model.Question;
import org.apache.ibatis.annotations.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ALTERUI on 2018/12/21 19:16
 */
@Mapper
public interface FeedDAO {
    String TABLE_NAME = " feed ";
    String INSERT_FIELDS = " type,user_id,created_date,data ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    /**
     * 添加一个新鲜事
     * @param feed
     * @return
     */
    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{type},#{userId},#{createdDate},#{data})"})
    int addFeed(Feed feed);



    @Select({"select "+SELECT_FIELDS+" from "+TABLE_NAME+" where id = #{id}"})
    Feed selectFeedById(int id);

    List<Feed> selectFeedByIds(@Param("maxId") int maxId,
                                    @Param("userIds") List<Integer> userIds,
                                    @Param("count") int count);




}
