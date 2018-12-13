package com.nowcoder.dao;

import com.nowcoder.model.Comment;
import com.nowcoder.model.Question;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Created by ALTERUI on 2018/11/29 14:56
 */

@Mapper
public interface CommentDAO {
    String TABLE_NAME = " comment ";
    String INSERT_FIELDS = " user_id, content, created_date, entity_id, entity_type,status";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    /**
     * 增加一条评论
     * @param comment
     * @return
     */
    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{userId},#{content},#{createdDate},#{entityId},#{entityType},#{status})"})
    int addComment(Comment comment);

    /**
     * 获取所有评论
     *
     * @param offset 从第几页开始
     * @param limit  一页几条数据
     * @return
     */
    @Select({"select "+SELECT_FIELDS+" from "+TABLE_NAME+" " +
            "where  entity_id= #{entityId} and entity_type = #{entityType} order by created_date desc " +
            "limit #{offset},#{limit}"})
    List<Comment> selectCommentsByEntity(@Param("entityId") int entityId,
                                          @Param("entityType") int entityType,
                                          @Param("offset") int offset,
                                          @Param("limit") int limit);

    /**
     * 获取评论的数量
     * @param entityId
     * @param entityType
     * @return
     */
    @Select({"select count(id) from "+TABLE_NAME+" " +
            "where  entity_id= #{entityId} and entity_type = #{entityType} "})
   int getCommentCounntByEntity(@Param("entityId") int entityId,
                                @Param("entityType") int entityType);


    @Update({"update "+TABLE_NAME+" set status=#{status} where id=#{id}"})
    int updateCommentStatus(@Param("status") int status,
                            @Param("id") int id);

    @Select({"select "+SELECT_FIELDS+" from "+TABLE_NAME+" " +
            "where  id=#{id}"})
    Comment getCommentById(int id);
}
