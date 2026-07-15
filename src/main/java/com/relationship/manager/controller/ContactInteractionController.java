package com.relationship.manager.controller;

import com.relationship.manager.common.ApiResponse;
import com.relationship.manager.common.PageResult;
import com.relationship.manager.config.JwtAuthenticationFilter;
import com.relationship.manager.dto.ContactInteractionRequest;
import com.relationship.manager.entity.ContactInteraction;
import com.relationship.manager.service.ContactInteractionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/contacts/{contactId}/interactions")
public class ContactInteractionController {

    private final ContactInteractionService interactionService;

    public ContactInteractionController(ContactInteractionService interactionService) {
        this.interactionService = interactionService;
    }

    @PostMapping
    public ApiResponse<ContactInteraction> create(
            @RequestAttribute(JwtAuthenticationFilter.USER_ID_ATTRIBUTE) Long userId,
            @PathVariable @Positive(message = "联系人ID必须大于0") Long contactId,
            @Valid @RequestBody ContactInteractionRequest request) {
        return ApiResponse.success(interactionService.create(userId, contactId, request));
    }

    @GetMapping
    public ApiResponse<PageResult<ContactInteraction>> list(
            @RequestAttribute(JwtAuthenticationFilter.USER_ID_ATTRIBUTE) Long userId,
            @PathVariable @Positive(message = "联系人ID必须大于0") Long contactId,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于0")
            @Max(value = 1000000, message = "页码过大") int page,
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "每页数量必须大于0")
            @Max(value = 100, message = "每页最多100条") int size) {
        return ApiResponse.success(interactionService.list(userId, contactId, page, size));
    }

    @PutMapping("/{interactionId}")
    public ApiResponse<ContactInteraction> update(
            @RequestAttribute(JwtAuthenticationFilter.USER_ID_ATTRIBUTE) Long userId,
            @PathVariable @Positive(message = "联系人ID必须大于0") Long contactId,
            @PathVariable @Positive(message = "联系记录ID必须大于0") Long interactionId,
            @Valid @RequestBody ContactInteractionRequest request) {
        return ApiResponse.success(interactionService.update(
                userId, contactId, interactionId, request));
    }

    @DeleteMapping("/{interactionId}")
    public ApiResponse<Void> delete(
            @RequestAttribute(JwtAuthenticationFilter.USER_ID_ATTRIBUTE) Long userId,
            @PathVariable @Positive(message = "联系人ID必须大于0") Long contactId,
            @PathVariable @Positive(message = "联系记录ID必须大于0") Long interactionId) {
        interactionService.delete(userId, contactId, interactionId);
        return ApiResponse.success();
    }
}
