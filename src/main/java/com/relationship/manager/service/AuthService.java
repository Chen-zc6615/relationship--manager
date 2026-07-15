package com.relationship.manager.service;

import com.relationship.manager.dto.LoginResponse;
import com.relationship.manager.dto.UserResponse;
import com.relationship.manager.dto.WechatSessionResponse;
import com.relationship.manager.entity.User;
import com.relationship.manager.exception.BusinessException;
import com.relationship.manager.mapper.UserMapper;
import com.relationship.manager.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class AuthService {

    private static final String WECHAT_CODE_TO_SESSION_URL =
            "https://api.weixin.qq.com/sns/jscode2session";

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final RestClient.Builder restClientBuilder;

    public AuthService(UserMapper userMapper, JwtUtil jwtUtil, RestClient.Builder restClientBuilder) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
        this.restClientBuilder = restClientBuilder;
    }

    @Value("${wechat.mini-program.app-id}")
    private String appId;

    @Value("${wechat.mini-program.app-secret}")
    private String appSecret;

    public LoginResponse wechatLogin(String code) {
        if (!StringUtils.hasText(appId) || !StringUtils.hasText(appSecret)) {
            throw new BusinessException(500, "服务端尚未配置微信小程序 AppID 或 AppSecret");
        }

        WechatSessionResponse wechatSession = requestWechatSession(code);
        User user = findOrCreateUser(wechatSession.getOpenid());
        String token = jwtUtil.generateToken(user.getId());
        return new LoginResponse(token, "Bearer", jwtUtil.getExpirationSeconds(), UserResponse.from(user));
    }

    private WechatSessionResponse requestWechatSession(String code) {
        String uri = UriComponentsBuilder.fromUriString(WECHAT_CODE_TO_SESSION_URL)
                .queryParam("appid", appId)
                .queryParam("secret", appSecret)
                .queryParam("js_code", code)
                .queryParam("grant_type", "authorization_code")
                .build()
                .encode()
                .toUriString();

        try {
            WechatSessionResponse response = restClientBuilder.build()
                    .get()
                    .uri(uri)
                    .retrieve()
                    .body(WechatSessionResponse.class);

            if (response == null) {
                throw new BusinessException(502, "微信登录服务没有返回数据");
            }
            if (!response.isSuccessful() || !StringUtils.hasText(response.getOpenid())) {
                String message = StringUtils.hasText(response.getErrmsg())
                        ? response.getErrmsg()
                        : "微信登录凭证无效";
                throw new BusinessException(401, "微信登录失败：" + message);
            }
            return response;
        } catch (BusinessException exception) {
            throw exception;
        } catch (RestClientException exception) {
            throw new BusinessException(502, "暂时无法连接微信登录服务");
        }
    }

    private User findOrCreateUser(String openId) {
        User existing = userMapper.findByOpenId(openId);
        if (existing != null) {
            return existing;
        }

        User user = new User();
        user.setOpenId(openId);
        user.setNickname("微信用户");
        try {
            userMapper.insert(user);
            return userMapper.findById(user.getId());
        } catch (DuplicateKeyException exception) {
            return userMapper.findByOpenId(openId);
        }
    }
}
