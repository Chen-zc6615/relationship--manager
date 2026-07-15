package com.relationship.manager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class ContactInteractionRequest {

    @NotBlank(message = "联系方式不能为空")
    @Pattern(
            regexp = "wechat|phone|sms|in_person|email|other",
            message = "联系方式必须是 wechat、phone、sms、in_person、email 或 other"
    )
    private String contactMethod;

    @NotNull(message = "联系时间不能为空")
    private LocalDateTime contactedAt;

    @Size(max = 5000, message = "联系备注不能超过5000个字符")
    private String notes;

    public String getContactMethod() {
        return contactMethod;
    }

    public void setContactMethod(String contactMethod) {
        this.contactMethod = contactMethod;
    }

    public LocalDateTime getContactedAt() {
        return contactedAt;
    }

    public void setContactedAt(LocalDateTime contactedAt) {
        this.contactedAt = contactedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
