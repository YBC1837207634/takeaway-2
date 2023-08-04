package com.example.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

/**
 * JWT 构建工具
 *
 *
 */
public class JWTUtil {

    // 密钥 secret
    private static final SecretKey secret = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    /**
     * 根据 payload 来生成 token
     * @return
     */
    public static String produce(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 3600))  // token 过期时间
                .signWith(secret) // 设置算法与密钥
                .compact();
    }

    /**
     * 解析 jwt
     * @param jwt
     * @return
     */
    public static Claims parser(String jwt) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(jwt).getBody();
    }

}
