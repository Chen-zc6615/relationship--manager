package com.relationship.manager.dto;

import com.relationship.manager.entity.User;
public class UserResponse {

    private Long id;
    private String nickname;
    private String avatarUrl;

    public UserResponse(Long id, String nickname, String avatarUrl) {
        this.id = id;
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
    }

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getNickname(), user.getAvatarUrl());
    }

    public Long getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
