package com.zjsu.yyd.course.gatewayservice.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Gateway 不生成 token（保留但不用）
    public String generateToken(String username) {
        throw new UnsupportedOperationException("Gateway 不负责生成 Token");
    }

    /** 解析 token，返回 Claims */
    public Claims parseToken(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /** 校验 token 是否合法 */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    /** ⭐ 从 token 中获取 username（subject） */
    public String getUsername(String token) {
        try {
            return parseToken(token).getSubject();
        } catch (JwtException e) {
            return null;
        }
    }

    /** ⭐⭐ 从 token 中获取 userId（自定义 claim） */
    public Long getUserId(String token) {
        try {
            Object userId = parseToken(token).get("userId");
            return userId == null ? null : Long.valueOf(userId.toString());
        } catch (JwtException e) {
            return null;
        }
    }
}
