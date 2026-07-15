package com.relationship.manager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class ContactRequest {

    @NotBlank(message = "联系人姓名不能为空")
    @Size(max = 100, message = "联系人姓名不能超过100个字符")
    private String name;

    @Size(max = 50, message = "关系类型不能超过50个字符")
    private String relationshipType;

    private LocalDate birthday;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

}
