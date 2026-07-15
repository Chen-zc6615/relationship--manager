package com.relationship.manager.service;

import com.relationship.manager.common.PageResult;
import com.relationship.manager.dto.ContactInteractionRequest;
import com.relationship.manager.entity.ContactInteraction;
import com.relationship.manager.exception.BusinessException;
import com.relationship.manager.mapper.ContactInteractionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ContactInteractionService {

    private final ContactInteractionMapper interactionMapper;
    private final ContactService contactService;

    public ContactInteractionService(ContactInteractionMapper interactionMapper,
                                     ContactService contactService) {
        this.interactionMapper = interactionMapper;
        this.contactService = contactService;
    }

    @Transactional
    public ContactInteraction create(Long userId, Long contactId,
                                     ContactInteractionRequest request) {
        contactService.get(userId, contactId);
        ContactInteraction interaction = new ContactInteraction();
        interaction.setContactId(contactId);
        applyRequest(interaction, request);
        interactionMapper.insert(interaction);
        return interactionMapper.findByIdAndContactId(interaction.getId(), contactId);
    }

    public PageResult<ContactInteraction> list(Long userId, Long contactId,
                                               int page, int size) {
        contactService.get(userId, contactId);
        int offset = (page - 1) * size;
        List<ContactInteraction> items = interactionMapper.findPage(contactId, offset, size);
        long total = interactionMapper.count(contactId);
        return new PageResult<>(items, total, page, size);
    }

    @Transactional
    public ContactInteraction update(Long userId, Long contactId, Long interactionId,
                                     ContactInteractionRequest request) {
        contactService.get(userId, contactId);
        ContactInteraction interaction = get(contactId, interactionId);
        applyRequest(interaction, request);
        int affectedRows = interactionMapper.update(interaction);
        if (affectedRows == 0) {
            throw new BusinessException(404, "联系记录不存在");
        }
        return interactionMapper.findByIdAndContactId(interactionId, contactId);
    }

    @Transactional
    public void delete(Long userId, Long contactId, Long interactionId) {
        contactService.get(userId, contactId);
        int affectedRows = interactionMapper.delete(interactionId, contactId);
        if (affectedRows == 0) {
            throw new BusinessException(404, "联系记录不存在");
        }
    }

    private ContactInteraction get(Long contactId, Long interactionId) {
        ContactInteraction interaction = interactionMapper.findByIdAndContactId(
                interactionId, contactId);
        if (interaction == null) {
            throw new BusinessException(404, "联系记录不存在");
        }
        return interaction;
    }

    private void applyRequest(ContactInteraction interaction,
                              ContactInteractionRequest request) {
        interaction.setContactMethod(request.getContactMethod().trim());
        interaction.setContactedAt(request.getContactedAt());
        interaction.setNotes(normalize(request.getNotes()));
    }

    private String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
