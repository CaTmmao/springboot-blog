package com.example.springboot.mapper;

import com.example.springboot.entities.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM user WHERE username = #{username}")
    User findUserByUsername(@Param("username") String username);

    @Select("INSERT INTO user(username, pwd, created_at, updated_at) values(#{username}, #{pwd}, now(), now())")
    void save(@Param("username") String username, @Param("pwd") String password);
}
