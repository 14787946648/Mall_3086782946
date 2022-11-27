package com.aurora.gateway.jwt;

import cn.hutool.core.util.StrUtil;
import com.aurora.enumeration.CodeEnum;
import com.aurora.exception.BusinessException;
import com.aurora.response.CommonResult;
import com.aurora.gateway.util.JwtUtil;
import com.aurora.gateway.util.Md5Util;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * @Description Token鉴权过滤
 * @Date 2022-11-22 2:06
 * @Version v1.0
 * @Author wh14787946648@outlook.com
 **/
@Data
@Component
@ConfigurationProperties(prefix = "url-blocking")
@Slf4j
public class GlobaAuthJwtFilter implements GlobalFilter, Ordered {

    /**
     * 无需鉴权的URL
     */
    private List<String> ignores;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 跳过不需要验证的路径
        if (Objects.nonNull(ignores) && isSkipUrl(exchange.getRequest().getURI().getPath())) {
            return chain.filter(exchange);
        }
        CommonResult<String> result = new CommonResult<>();
        // 从请求头中取得token
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        // 标识当前请求来源于网关，屏蔽非法请求
        exchange.getRequest().mutate().header("RequestSource", Md5Util.encryption("from_gateway")).build();
        if (StrUtil.hasBlank(token)) {
            // 无效签名
            return getFailResponse(exchange.getResponse(), CommonResult.fail(CodeEnum.JWT_INVALID.getCode(), CodeEnum.JWT_INVALID.getMessage() + ":token= " + token));
        }
        try {
            // 验证令牌
            JwtUtil.verify(token);
            // 权限校验
            // 放行请求
            return chain.filter(exchange);
        } catch (SignatureVerificationException e) {
            // 无效签名
            result.setCode(CodeEnum.JWT_INVALID.getCode());
            result.setData(CodeEnum.JWT_INVALID.getMessage());
        } catch (TokenExpiredException e) {
            // token过期
            result.setCode(CodeEnum.JWT_OVERDUE.getCode());
            result.setData(CodeEnum.JWT_OVERDUE.getMessage());
        } catch (AlgorithmMismatchException e) {
            // token算法不一致
            result.setCode(CodeEnum.JWT_ALGORITHM_INCONSISTENCY.getCode());
            result.setData(CodeEnum.JWT_ALGORITHM_INCONSISTENCY.getMessage());
        } catch (Exception e) {
            // token失效
            result.setCode(CodeEnum.JWT_LOSE_EFFECT.getCode());
            result.setData(CodeEnum.JWT_LOSE_EFFECT.getMessage());
        }
        return getFailResponse(exchange.getResponse(), result);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * 判断当前访问的url是否开头URI是在配置的忽略url列表中
     *
     * @param url 目标URL
     * @return boolean
     */
    public boolean isSkipUrl(String url) {
        for (String skipAuthUrl : ignores) {
            if (url.startsWith(skipAuthUrl)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    /**
     * 获取失败返回信息
     *
     * @param response ServerHttpResponse
     * @param result   CommonResult<String>
     * @return Mono<Void>
     */
    private Mono<Void> getFailResponse(ServerHttpResponse response, CommonResult<String> result) {
        try {
            //将map转化成json，response使用的是Jackson
            String resultStr = new ObjectMapper().writeValueAsString(result);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            response.getHeaders().set("Access-Control-Allow-Origin", "*");
            response.setStatusCode(HttpStatus.OK);
            response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
            return response.writeWith(Flux.just(response.bufferFactory().wrap(resultStr.getBytes(StandardCharsets.UTF_8))));
        } catch (Exception e) {
            log.error("Gateway authentication failed.", e);
            throw new BusinessException(CodeEnum.ERROR, "Gateway authentication failed.");
        }
    }
}


