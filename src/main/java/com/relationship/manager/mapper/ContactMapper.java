package com.relationship.manager.mapper;

import com.relationship.manager.entity.Contact;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface ContactMapper {

    @Insert("""
            INSERT INTO contacts
                (user_id, name, phone, email, relationship_type, birthday, notes)
            VALUES
                (#{userId}, #{name}, #{phone}, #{email}, #{relationshipType}, #{birthday}, #{notes})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Contact contact);

    @Select("""
            SELECT id, user_id, name, phone, email, relationship_type, birthday,
                   notes, created_at, updated_at
            FROM contacts
            WHERE id = #{id} AND user_id = #{userId}
            """)
    Contact findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Select({
            "<script>",
            "SELECT id, user_id, name, phone, email, relationship_type, birthday,",
            "       notes, created_at, updated_at",
            "FROM contacts",
            "WHERE user_id = #{userId}",
            "<if test='keyword != null and keyword != \"\"'>",
            "  AND (name LIKE CONCAT('%', #{keyword}, '%')",
            "       OR phone LIKE CONCAT('%', #{keyword}, '%')",
            "       OR email LIKE CONCAT('%', #{keyword}, '%')",
            "       OR relationship_type LIKE CONCAT('%', #{keyword}, '%'))",
            "</if>",
            "ORDER BY updated_at DESC, id DESC",
            "LIMIT #{size} OFFSET #{offset}",
            "</script>"
    })
    List<Contact> findPage(@Param("userId") Long userId,
                           @Param("keyword") String keyword,
                           @Param("offset") int offset,
                           @Param("size") int size);

    @Select({
            "<script>",
            "SELECT COUNT(*) FROM contacts",
            "WHERE user_id = #{userId}",
            "<if test='keyword != null and keyword != \"\"'>",
            "  AND (name LIKE CONCAT('%', #{keyword}, '%')",
            "       OR phone LIKE CONCAT('%', #{keyword}, '%')",
            "       OR email LIKE CONCAT('%', #{keyword}, '%')",
            "       OR relationship_type LIKE CONCAT('%', #{keyword}, '%'))",
            "</if>",
            "</script>"
    })
    long count(@Param("userId") Long userId, @Param("keyword") String keyword);

    @Update("""
            UPDATE contacts
            SET name = #{name},
                phone = #{phone},
                email = #{email},
                relationship_type = #{relationshipType},
                birthday = #{birthday},
                notes = #{notes},
                updated_at = CURRENT_TIMESTAMP
            WHERE id = #{id} AND user_id = #{userId}
            """)
    int update(Contact contact);

    @Delete("DELETE FROM contacts WHERE id = #{id} AND user_id = #{userId}")
    int delete(@Param("id") Long id, @Param("userId") Long userId);
}
