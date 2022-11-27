package com.aurora.gateway.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.Map;

/**
 * @Description JWT工具类
 * @Date 2022-11-22 1:55
 * @Version v1.0
 * @Author wh14787946648@outlook.com
 **/
public class JwtUtil {

    /**
     * 密钥
     */
    private static final String SIGNATURE = "323123213123213123313";

    /**
     * 过期时间
     */
    public static final Integer EXPIRATION_TIME = 24 * 60 * 60;

    /**
     * 生成token
     *
     * @param payload token需要携带的信息
     * @return token字符串
     */
    public static String getToken(Map<String, String> payload) {
        // 指定token过期时间为1天
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, EXPIRATION_TIME);
        JWTCreator.Builder builder = JWT.create();
        // 构建payload
        payload.forEach(builder::withClaim);
        // 指定过期时间和签名算法
        return builder.withExpiresAt(calendar.getTime()).sign(Algorithm.HMAC256(SIGNATURE));
    }

    /**
     * 验证token
     *
     * @param token token
     */
    public static void verify(String token) {
        JWT.require(Algorithm.HMAC256(SIGNATURE)).build().verify(token);
    }

    /**
     * 获取token中payload
     *
     * @param token token
     * @return DecodedJWT
     */
    public static DecodedJWT getToken(String token) {
        return JWT.require(Algorithm.HMAC256(SIGNATURE)).build().verify(token);
    }

}


