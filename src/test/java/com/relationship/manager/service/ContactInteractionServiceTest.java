package com.relationship.manager.service;

import com.relationship.manager.dto.ContactInteractionRequest;
import com.relationship.manager.entity.ContactInteraction;
import com.relationship.manager.mapper.ContactInteractionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContactInteractionServiceTest {

    @Mock
    private ContactInteractionMapper interactionMapper;

    @Mock
    private ContactService contactService;

    private ContactInteractionService interactionService;

    @BeforeEach
    void setUp() {
        interactionService = new ContactInteractionService(interactionMapper, contactService);
    }

    @Test
    void shouldVerifyContactOwnershipAndCreateInteraction() {
        ContactInteractionRequest request = new ContactInteractionRequest();
        request.setContactMethod("wechat");
        request.setContactedAt(LocalDateTime.of(2026, 7, 15, 14, 0));
        request.setNotes("  讨论下周见面的安排  ");

        when(interactionMapper.insert(any(ContactInteraction.class))).thenAnswer(invocation -> {
            ContactInteraction interaction = invocation.getArgument(0);
            interaction.setId(9L);
            return 1;
        });
        when(interactionMapper.findByIdAndContactId(9L, 2L)).thenAnswer(invocation -> {
            ContactInteraction interaction = new ContactInteraction();
            interaction.setId(9L);
            interaction.setContactId(2L);
            interaction.setContactMethod("wechat");
            interaction.setContactedAt(request.getContactedAt());
            interaction.setNotes("讨论下周见面的安排");
            return interaction;
        });

        ContactInteraction created = interactionService.create(1L, 2L, request);

        verify(contactService).get(1L, 2L);
        assertEquals(9L, created.getId());
        assertEquals("wechat", created.getContactMethod());
        assertEquals("讨论下周见面的安排", created.getNotes());
    }
}
