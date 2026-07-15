package com.relationship.manager.service;

import com.relationship.manager.common.PageResult;
import com.relationship.manager.dto.ContactRequest;
import com.relationship.manager.entity.Contact;
import com.relationship.manager.exception.BusinessException;
import com.relationship.manager.mapper.ContactMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ContactService {

    private final ContactMapper contactMapper;

    public ContactService(ContactMapper contactMapper) {
        this.contactMapper = contactMapper;
    }

    @Transactional
    public Contact create(Long userId, ContactRequest request) {
        Contact contact = new Contact();
        contact.setUserId(userId);
        applyRequest(contact, request);
        contactMapper.insert(contact);
        return contactMapper.findByIdAndUserId(contact.getId(), userId);
    }

    public Contact get(Long userId, Long contactId) {
        Contact contact = contactMapper.findByIdAndUserId(contactId, userId);
        if (contact == null) {
            throw new BusinessException(404, "联系人不存在");
        }
        return contact;
    }

    public PageResult<Contact> list(Long userId, String keyword, int page, int size) {
        String normalizedKeyword = normalize(keyword);
        int offset = (page - 1) * size;
        List<Contact> items = contactMapper.findPage(userId, normalizedKeyword, offset, size);
        long total = contactMapper.count(userId, normalizedKeyword);
        return new PageResult<>(items, total, page, size);
    }

    @Transactional
    public Contact update(Long userId, Long contactId, ContactRequest request) {
        Contact contact = get(userId, contactId);
        applyRequest(contact, request);
        int affectedRows = contactMapper.update(contact);
        if (affectedRows == 0) {
            throw new BusinessException(404, "联系人不存在");
        }
        return contactMapper.findByIdAndUserId(contactId, userId);
    }

    @Transactional
    public void delete(Long userId, Long contactId) {
        int affectedRows = contactMapper.delete(contactId, userId);
        if (affectedRows == 0) {
            throw new BusinessException(404, "联系人不存在");
        }
    }

    private void applyRequest(Contact contact, ContactRequest request) {
        contact.setName(request.getName().trim());
        contact.setPhone(normalize(request.getPhone()));
        contact.setEmail(normalize(request.getEmail()));
        contact.setRelationshipType(normalize(request.getRelationshipType()));
        contact.setBirthday(request.getBirthday());
        contact.setNotes(normalize(request.getNotes()));
    }

    private String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
