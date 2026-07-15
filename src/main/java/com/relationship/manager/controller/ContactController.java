package com.relationship.manager.controller;

import com.relationship.manager.common.ApiResponse;
import com.relationship.manager.common.PageResult;
import com.relationship.manager.config.JwtAuthenticationFilter;
import com.relationship.manager.dto.ContactRequest;
import com.relationship.manager.entity.Contact;
import com.relationship.manager.service.ContactService;
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
@RequestMapping("/api/contacts")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    public ApiResponse<Contact> create(
            @RequestAttribute(JwtAuthenticationFilter.USER_ID_ATTRIBUTE) Long userId,
            @Valid @RequestBody ContactRequest request) {
        return ApiResponse.success(contactService.create(userId, request));
    }

    @GetMapping
    public ApiResponse<PageResult<Contact>> list(
            @RequestAttribute(JwtAuthenticationFilter.USER_ID_ATTRIBUTE) Long userId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于0")
            @Max(value = 1000000, message = "页码过大") int page,
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "每页数量必须大于0")
            @Max(value = 100, message = "每页最多100条") int size) {
        return ApiResponse.success(contactService.list(userId, keyword, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<Contact> get(
            @RequestAttribute(JwtAuthenticationFilter.USER_ID_ATTRIBUTE) Long userId,
            @PathVariable @Positive(message = "联系人ID必须大于0") Long id) {
        return ApiResponse.success(contactService.get(userId, id));
    }

    @PutMapping("/{id}")
    public ApiResponse<Contact> update(
            @RequestAttribute(JwtAuthenticationFilter.USER_ID_ATTRIBUTE) Long userId,
            @PathVariable @Positive(message = "联系人ID必须大于0") Long id,
            @Valid @RequestBody ContactRequest request) {
        return ApiResponse.success(contactService.update(userId, id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @RequestAttribute(JwtAuthenticationFilter.USER_ID_ATTRIBUTE) Long userId,
            @PathVariable @Positive(message = "联系人ID必须大于0") Long id) {
        contactService.delete(userId, id);
        return ApiResponse.success();
    }
}
