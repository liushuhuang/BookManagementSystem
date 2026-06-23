package com.book.service;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.book.common.BusinessException;
import com.book.domain.entity.SysUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class TokenService {

    private final byte[] secret;

    public TokenService(@Value("${book.security.jwt-secret}") String tokenSecret) {
        this.secret = tokenSecret.getBytes(StandardCharsets.UTF_8);
    }

    public String createToken(SysUser user) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("uid", user.getId());
        return JWTUtil.createToken(payload, secret);
    }

    public Long parseUserId(String token) {
        try {
            JWT jwt = JWTUtil.parseToken(token);
            if (!jwt.setKey(secret).verify()) {
                throw new BusinessException(401, "未登录或登录凭证无效");
            }
            Object uid = jwt.getPayload("uid");
            if (uid == null) {
                throw new BusinessException(401, "未登录或登录凭证无效");
            }
            return Long.valueOf(String.valueOf(uid));
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(401, "未登录或登录凭证无效");
        }
    }
}
