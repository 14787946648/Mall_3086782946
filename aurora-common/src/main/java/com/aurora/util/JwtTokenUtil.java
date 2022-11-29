package com.aurora.util;

import com.aurora.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description JWT Token工具类
 * @Date 2022-11-29 3:08
 * @Version v1.0
 * @Author wh14787946648@outlook.com
 **/
@Component
@SuppressWarnings("all")
public class JwtTokenUtil {
    /**
     * 判断令牌是否过期
     *
     * @param token 令牌
     * @return true=已过期，false=未过期
     */
    public Boolean isTokenExpired(String token) {
        try {
            return Jwts.parser().setSigningKey(jwtProperties.getSecret()).parseClaimsJws(token).getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            //验证 JWT 签名失败等同于令牌过期
        }
        return true;
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return UserId
     */
    public String getUserIdFromToken(String token) {
        try {
            return Jwts.parser().setSigningKey(jwtProperties.getSecret()).parseClaimsJws(token).getBody().getSubject();
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return UserName
     */
    public String getUserNameFromToken(String token) {
        try {
            return (String) Jwts.parser().setSigningKey(jwtProperties.getSecret()).parseClaimsJws(token).getBody().get(USER_NAME);
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * 生成 token 令牌
     *
     * @param userId 用户Id或用户名
     * @return 令token牌
     */

    public Map<String, Object> generateTokenAndRefreshToken(String userId, String username) {
        Map<String, Object> tokenMap = buildToken(userId, username);
        cacheToken(userId, tokenMap);
        return tokenMap;
    }

    /**
     * 刷新并生成令牌
     *
     * @param userId   userId
     * @param username username
     * @return Map<String, Object>
     */
    public Map<String, Object> refreshTokenAndGenerateToken(String userId, String username) {
        Map<String, Object> tokenMap = buildToken(userId, username);
        stringRedisTemplate.delete(JWT_CACHE_KEY + userId);
        cacheToken(userId, tokenMap);
        return tokenMap;
    }

    /**
     * 删除Redis中Token
     *
     * @param userId userId
     * @return 删除结果
     */
    public boolean removeToken(String userId) {
        return Boolean.TRUE.equals(stringRedisTemplate.delete(JWT_CACHE_KEY + userId));
    }

    /**
     * 刷新令牌
     *
     * @param token 原令牌
     * @return 新令牌
     */
    public String refreshToken(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(jwtProperties.getSecret()).parseClaimsJws(token).getBody();
            claims.put("created", new Date());
            return generateToken(claims);
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * 构建buildClaims
     *
     * @param userId   String
     * @param username String
     * @param payloads map
     * @return Map
     */
    private Map<String, Object> buildClaims(String userId, String username, Map<String, String> payloads) {
        int payloadSizes = payloads == null ? 0 : payloads.size();
        Map<String, Object> claims = new HashMap<>(payloadSizes + 2);
        claims.put("sub", userId);
        claims.put("username", username);
        claims.put("created", new Date());
        if (payloadSizes > 0) {
            claims.putAll(payloads);
        }
        return claims;
    }

    /**
     * 判断令牌是否不存在 redis 中
     *
     * @param token 刷新令牌
     * @return true=不存在，false=存在
     */
    public Boolean isRefreshTokenNotExistCache(String token) {
        String userId = getUserIdFromToken(token);
        String refreshToken = (String) stringRedisTemplate.opsForHash().get(JWT_CACHE_KEY + userId, REFRESH_TOKEN);
        return refreshToken == null || !refreshToken.equals(token);
    }

    /**
     * 验证令牌
     *
     * @param token  令牌
     * @param userId 用户Id用户名
     * @return 是否有效
     */
    public Boolean validateToken(String token, String userId) {
        String username = getUserIdFromToken(token);
        return (username.equals(userId) && !isTokenExpired(token));
    }

    /**
     * 缓存Token
     *
     * @param userId   userId
     * @param tokenMap tokenMap
     */
    private void cacheToken(String userId, Map<String, Object> tokenMap) {
        stringRedisTemplate.opsForHash().put(JWT_CACHE_KEY + userId, ACCESS_TOKEN, tokenMap.get(ACCESS_TOKEN));
        stringRedisTemplate.opsForHash().put(JWT_CACHE_KEY + userId, REFRESH_TOKEN, tokenMap.get(REFRESH_TOKEN));
        stringRedisTemplate.expire(userId, jwtProperties.getExpiration() * 2, TimeUnit.MILLISECONDS);
    }

    /**
     * 构建Token
     *
     * @param userId   String
     * @param username String
     * @return Map
     */
    private Map<String, Object> buildToken(String userId, String username) {
        HashMap<String, Object> tokenMap = new HashMap<>(2);
        tokenMap.put(ACCESS_TOKEN, generateToken(buildClaims(userId, username, null)));
        tokenMap.put(REFRESH_TOKEN, generateRefreshToken(buildClaims(userId, username, null)));
        tokenMap.put(EXPIRE_IN, jwtProperties.getExpiration());
        return tokenMap;
    }

    /**
     * 生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    private String generateToken(Map<String, Object> claims) {
        Date expirationDate = new Date(System.currentTimeMillis() + jwtProperties.getExpiration());
        return Jwts.builder().setClaims(claims)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getSecret())
                .compact();
    }

    /**
     * 生成刷新令牌 refreshToken，有效期是令牌的 2 倍
     *
     * @param claims 数据声明
     * @return 令牌
     */
    private String generateRefreshToken(Map<String, Object> claims) {
        Date expirationDate = new Date(System.currentTimeMillis() + jwtProperties.getExpiration() * 2);
        return Jwts.builder().setClaims(claims)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getSecret())
                .compact();
    }


    private static final String JWT_CACHE_KEY = "jwt:userId:";
    private static final String USER_NAME = "username";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String EXPIRE_IN = "expire_in";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private JwtProperties jwtProperties;
}
