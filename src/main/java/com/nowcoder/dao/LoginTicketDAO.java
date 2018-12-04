package com.nowcoder.dao;

import com.nowcoder.model.LoginTicket;
import org.apache.ibatis.annotations.*;

/**
 * Created by ALTERUI on 2018/11/5 16:33
 */

@Mapper
public interface LoginTicketDAO {
    String TABLE_NAME = " login_ticket ";
    String INSERT_FIELDS = " user_id, ticket,expired,status ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;


    /**
     * 插入一条数据
     * #{}  --{}里面写类的字段！
     */
    @Insert({"insert into "+TABLE_NAME+"("+INSERT_FIELDS+
            ") values(#{userId},#{ticket},#{expired},#{status}) "})
    int addTicket(LoginTicket loginTicket);

    /**
     * 通过ticket查询loginTicket
     */

    @Select({"select " + SELECT_FIELDS + " from " + TABLE_NAME + " where ticket=#{ticket}"})
    LoginTicket selectByTicket(String ticket);

    /**
     * 登出时更新status的状态
     */

    @Update({"update "+TABLE_NAME+" set status=#{status} where ticket=#{ticket}"})
    void updateStatus(@Param("ticket") String ticket,
                      @Param("status") int status);



}
