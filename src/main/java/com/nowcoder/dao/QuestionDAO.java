package com.nowcoder.dao;

import com.nowcoder.model.Question;
import com.nowcoder.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by ALTERUI on 2018/11/4
 */

@Mapper
public interface QuestionDAO {
    String TABLE_NAME = " question ";
    String INSERT_FIELDS = " title, content, created_date, user_id, comment_count ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{title},#{content},#{createdDate},#{userId},#{commentCount})"})
    int addQuestion(Question question);

    List<Question> selectLatestQuestions(@Param("userId") int userId);

    @Select({"select "+SELECT_FIELDS+" from "+TABLE_NAME+" where id = #{id}"})
    Question selectQuestionById(int id);

    @Update({"update " +TABLE_NAME +" set comment_count = #{commentCount} where id = #{id}"})
    int updateCommentCount(@Param("id") int id,
                           @Param("commentCount") int commentCount);

    List<Question> getLatestQuestionsAndKeyWord(@Param("userId")int userId,@Param("keyword") String keyword);
}
