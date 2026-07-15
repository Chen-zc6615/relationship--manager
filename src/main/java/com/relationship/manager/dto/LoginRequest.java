package com.relationship.manager.dto;

import jakarta.validation.constraints.NotBlank;
public class LoginRequest {

    @NotBlank(message = "微信登录凭证不能为空")
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
