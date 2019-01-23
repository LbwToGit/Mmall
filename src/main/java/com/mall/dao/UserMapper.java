package com.mall.dao;

import com.mall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUsername(String username);

    int checkEmail(@Param("email") String email);

    String selectQuestionByName(@Param("username") String username);

    int checkAnser(@Param("username") String username,@Param("answer")String answer,@Param("question")String question);

    User selectLogin(@Param("username")String username,@Param("password")String password);

    int checkPassword(@Param("id")int id,@Param("passwordOld")String passwordOld);

    int updatePasswordByUsername(@Param("username") String username,@Param("passwordNew")String passwordNew);

    int checkEmailByUserId(@Param("id")int id,@Param("email")String email);
}