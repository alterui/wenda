package com.nowcoder.dao;

import com.nowcoder.model.Comment;
import com.nowcoder.model.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by ALTERUI on 2018/12/4 20:32
 */
@Mapper
public interface MessageDAO {


    String TABLE_NAME = " message ";
    String INSERT_FIELDS = " from_id, to_id, created_date, content, has_read, conversation_id";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    /**
     * 增加一条消息
     * @param
     * @return
     */
    @Insert({"insert into "+TABLE_NAME+" ( "+ INSERT_FIELDS+" ) " +
            "values ( #{fromId},#{toId},#{createdDate},#{content},#{hasRead},#{conversationId})"})
    int addMessage(Message message);

    /**
     * 获取会话详情列表
     *
     * @param offset 从第几页开始
     * @param limit  一页几条数据
     * @return
     */
    @Select({"select "+SELECT_FIELDS+" from "+TABLE_NAME+" " +
            "where  conversation_id= #{conversationId}  order by created_date desc " +
            "limit #{offset},#{limit}"})
    List<Message> selectMessagesByConversationId(@Param("conversationId") String conversationId,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);


    @Select({"select "+ INSERT_FIELDS+ " ,count(id) as id from ( select * from "+ TABLE_NAME +" where from_id=#{userId} " +
            "  or to_id=#{userId} order by id desc) tt group by conversation_id order by created_date desc " +
            " limit #{offset}, #{limit} "})
    List<Message> selectConversationList(@Param("userId") int userId,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);


    /**
     * 未读数目
     * @param userId
     * @param conversationId
     * @return
     */
    @Select({"select count(id) from "+TABLE_NAME+" where has_read=0 and to_id=#{userId} and conversation_id=#{conversationId}"})
    int getUnReadCount(@Param("userId") int userId,
                       @Param("conversationId") String conversationId);

    /**
     * 将has_read置为1，表示已经读过
     * @param userId
     * @param conversationId
     */
    @Update({"update "+ TABLE_NAME +" set has_read=1  where to_id=#{userId} and conversation_id=#{conversationId} "})
    void updateUnReadCount(@Param("userId") int userId,
                           @Param("conversationId") String conversationId);

}
