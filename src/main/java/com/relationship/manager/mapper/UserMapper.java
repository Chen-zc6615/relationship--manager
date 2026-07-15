package com.relationship.manager.mapper;

import com.relationship.manager.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserMapper {

    @Select("""
            SELECT id, open_id, nickname, avatar_url, created_at, updated_at
            FROM users
            WHERE open_id = #{openId}
            """)
    User findByOpenId(@Param("openId") String openId);

    @Insert("""
            INSERT INTO users (open_id, nickname, avatar_url)
            VALUES (#{openId}, #{nickname}, #{avatarUrl})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Select("""
            SELECT id, open_id, nickname, avatar_url, created_at, updated_at
            FROM users
            WHERE id = #{id}
            """)
    User findById(@Param("id") Long id);
}
