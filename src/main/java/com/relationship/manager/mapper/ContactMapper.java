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
                (user_id, name, relationship_type, birthday)
            VALUES
                (#{userId}, #{name}, #{relationshipType}, #{birthday})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Contact contact);

    @Select("""
            SELECT c.id, c.user_id, c.name, c.relationship_type, c.birthday,
                   c.created_at, c.updated_at,
                   (SELECT ci.contact_method
                    FROM contact_interactions ci
                    WHERE ci.contact_id = c.id
                    ORDER BY ci.contacted_at DESC, ci.id DESC
                    LIMIT 1) AS last_contact_method,
                   (SELECT ci.contacted_at
                    FROM contact_interactions ci
                    WHERE ci.contact_id = c.id
                    ORDER BY ci.contacted_at DESC, ci.id DESC
                    LIMIT 1) AS last_contacted_at
            FROM contacts c
            WHERE c.id = #{id} AND c.user_id = #{userId}
            """)
    Contact findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Select({
            "<script>",
            "SELECT c.id, c.user_id, c.name, c.relationship_type, c.birthday,",
            "       c.created_at, c.updated_at,",
            "       (SELECT ci.contact_method FROM contact_interactions ci",
            "        WHERE ci.contact_id = c.id",
            "        ORDER BY ci.contacted_at DESC, ci.id DESC LIMIT 1) AS last_contact_method,",
            "       (SELECT ci.contacted_at FROM contact_interactions ci",
            "        WHERE ci.contact_id = c.id",
            "        ORDER BY ci.contacted_at DESC, ci.id DESC LIMIT 1) AS last_contacted_at",
            "FROM contacts c",
            "WHERE c.user_id = #{userId}",
            "<if test='keyword != null and keyword != \"\"'>",
            "  AND (c.name LIKE CONCAT('%', #{keyword}, '%')",
            "       OR c.relationship_type LIKE CONCAT('%', #{keyword}, '%'))",
            "</if>",
            "ORDER BY c.updated_at DESC, c.id DESC",
            "LIMIT #{size} OFFSET #{offset}",
            "</script>"
    })
    List<Contact> findPage(@Param("userId") Long userId,
                           @Param("keyword") String keyword,
                           @Param("offset") int offset,
                           @Param("size") int size);

    @Select({
            "<script>",
            "SELECT COUNT(*) FROM contacts c",
            "WHERE c.user_id = #{userId}",
            "<if test='keyword != null and keyword != \"\"'>",
            "  AND (c.name LIKE CONCAT('%', #{keyword}, '%')",
            "       OR c.relationship_type LIKE CONCAT('%', #{keyword}, '%'))",
            "</if>",
            "</script>"
    })
    long count(@Param("userId") Long userId, @Param("keyword") String keyword);

    @Update("""
            UPDATE contacts
            SET name = #{name},
                relationship_type = #{relationshipType},
                birthday = #{birthday},
                updated_at = CURRENT_TIMESTAMP
            WHERE id = #{id} AND user_id = #{userId}
            """)
    int update(Contact contact);

    @Delete("DELETE FROM contacts WHERE id = #{id} AND user_id = #{userId}")
    int delete(@Param("id") Long id, @Param("userId") Long userId);
}
