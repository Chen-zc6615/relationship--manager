package com.relationship.manager.mapper;

import com.relationship.manager.entity.ContactInteraction;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface ContactInteractionMapper {

    @Insert("""
            INSERT INTO contact_interactions
                (contact_id, contact_method, contacted_at, notes)
            VALUES
                (#{contactId}, #{contactMethod}, #{contactedAt}, #{notes})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ContactInteraction interaction);

    @Select("""
            SELECT id, contact_id, contact_method, contacted_at, notes, created_at, updated_at
            FROM contact_interactions
            WHERE id = #{id} AND contact_id = #{contactId}
            """)
    ContactInteraction findByIdAndContactId(@Param("id") Long id,
                                             @Param("contactId") Long contactId);

    @Select("""
            SELECT id, contact_id, contact_method, contacted_at, notes, created_at, updated_at
            FROM contact_interactions
            WHERE contact_id = #{contactId}
            ORDER BY contacted_at DESC, id DESC
            LIMIT #{size} OFFSET #{offset}
            """)
    List<ContactInteraction> findPage(@Param("contactId") Long contactId,
                                      @Param("offset") int offset,
                                      @Param("size") int size);

    @Select("SELECT COUNT(*) FROM contact_interactions WHERE contact_id = #{contactId}")
    long count(@Param("contactId") Long contactId);

    @Update("""
            UPDATE contact_interactions
            SET contact_method = #{contactMethod},
                contacted_at = #{contactedAt},
                notes = #{notes},
                updated_at = CURRENT_TIMESTAMP
            WHERE id = #{id} AND contact_id = #{contactId}
            """)
    int update(ContactInteraction interaction);

    @Delete("""
            DELETE FROM contact_interactions
            WHERE id = #{id} AND contact_id = #{contactId}
            """)
    int delete(@Param("id") Long id, @Param("contactId") Long contactId);
}
