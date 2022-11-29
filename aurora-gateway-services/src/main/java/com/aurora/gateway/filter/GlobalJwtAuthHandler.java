package com.aurora.gateway.filter;

import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.JSON;
import com.aurora.common.CommonResult;
import com.aurora.constants.Constant;
import com.aurora.common.CodeEnum;
import com.aurora.util.JwtTokenUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @Description JWT全局过滤器
 * @Date 2022-11-29 2:29
 * @Version v1.0
 * @Author wh14787946648@outlook.com
 **/
@Slf4j
@Configuration
@Data
@ConfigurationProperties(prefix = "url-blocking")
public class GlobalJwtAuthHandler implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取exchange信息
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        ServerHttpResponse serverHttpResponse = exchange.getResponse();
        ServerHttpRequest.Builder mutate = serverHttpRequest.mutate();
        // 跳过对请求URL的 token 检查
        if (ignores.contains(serverHttpRequest.getURI().getPath())) {
            return chain.filter(exchange);
        }
        // 从 HTTP 请求头中获取 JWT 令牌
        String token = serverHttpRequest.getHeaders().getFirst(Constant.AUTHENTICATION);
        // 如果前端设置了令牌前缀，则裁剪掉前缀
        token = StringUtils.isNotEmpty(token) && token.startsWith(Constant.PREFIX) ?
                token.replaceFirst(Constant.PREFIX, StringUtils.EMPTY) :
                serverHttpRequest.getHeaders().getFirst(Constant.AUTHENTICATION);
        // 无效签名
        if (StrUtil.hasBlank(token)) {
            return unauthorizedResponse(exchange, serverHttpResponse, CommonResult.fail(CodeEnum.JWT_INVALID.getCode(), CodeEnum.JWT_INVALID.getMessage()));
        }
        // ToKen过期
        if (jwtTokenUtil.isTokenExpired(token)) {
            return unauthorizedResponse(exchange, serverHttpResponse, CommonResult.fail(CodeEnum.JWT_OVERDUE.getCode(), CodeEnum.JWT_OVERDUE.getMessage()));
        }
        // 验证 token 里面的 userId 是否为空
        String userId = jwtTokenUtil.getUserIdFromToken(token);
        String username = jwtTokenUtil.getUserNameFromToken(token);
        if (StringUtils.isEmpty(userId)) {
            return unauthorizedResponse(exchange, serverHttpResponse, CommonResult.fail(CodeEnum.JWT_ALGORITHM_INCONSISTENCY.getCode(), CodeEnum.JWT_ALGORITHM_INCONSISTENCY.getMessage()));
        }
        // 验证Token
        if (jwtTokenUtil.validateToken(token,userId)){
            // 设置用户信息到请求
            addHeader(mutate, USER_ID, userId);
            addHeader(mutate, USER_NAME, username);
            // 内部请求来源参数清除
            mutate.headers(httpHeaders -> httpHeaders.remove(FROM_SOURCE)).build();
            return chain.filter(exchange.mutate().request(mutate.build()).build());
        }
        return unauthorizedResponse(exchange, serverHttpResponse, CommonResult.fail(CodeEnum.JWT_LOSE_EFFECT.getCode(), CodeEnum.JWT_LOSE_EFFECT.getMessage()));
    }

    /**
     * 请求头添加签名信息
     *
     * @param mutate ServerHttpRequest
     * @param name   String
     * @param value  Object
     */
    private void addHeader(ServerHttpRequest.Builder mutate, String name, Object value) {
        if (value == null) {
            return;
        }
        mutate.header(name, URLEncoder.encode(value.toString(), StandardCharsets.UTF_8));
    }

    /**
     * 将 JWT 鉴权失败的消息响应给客户端
     *
     * @param exchange           ServerWebExchange
     * @param serverHttpResponse ServerHttpResponse
     * @param result             响应结果
     * @return Mono
     */
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, ServerHttpResponse serverHttpResponse, CommonResult<Object> result) {
        log.error("[鉴权异常处理]请求路径:{}", exchange.getRequest().getPath());
        serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
        serverHttpResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        DataBuffer dataBuffer = serverHttpResponse.bufferFactory()
                .wrap(JSON.toJSONStringWithDateFormat(result, JSON.DEFFAULT_DATE_FORMAT).getBytes(StandardCharsets.UTF_8));
        return serverHttpResponse.writeWith(Flux.just(dataBuffer));
    }

    /**
     * 无需鉴权的URL
     */
    private List<String> ignores;

    /**
     * JWT工具类
     */
    @Resource
    private JwtTokenUtil jwtTokenUtil;

    public static final String USER_ID = "userId";
    public static final String USER_NAME = "username";
    public static final String FROM_SOURCE = "from-source";

    @Override
    public int getOrder() {
        return -101;
    }
}

