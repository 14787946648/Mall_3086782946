package com.aurora.gateway.filter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.aurora.common.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * @Description 全局过滤实现类：全局异常处理
 * @Date 2022-11-27 1:23
 * @Version v1.0
 * @Author wh14787946648@outlook.com
 **/
@Configuration
@Slf4j
public class GlobalExceptionHandler implements FilterHandler {

    @Override
    public int getOrder() {
        return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 1;
    }

    /**
     * 重写过滤器
     *
     * @param exchange ServerWebExchange
     * @param chain    GatewayFilterChain
     * @return Mono<Void>
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 操作针对某些路由跳过全局过滤器
        if (Objects.nonNull(exchange.getAttributes().get(IS_IGNORE_AUTH_FILTER))) {
            return chain.filter(exchange);
        }
        // 包装响应体
        ServerWebExchange newExchange = exchange.mutate().response(
                ServerHttpResponseDecoratorHelper.build(exchange, (originalBody) -> {
                    MediaType responseMediaType = exchange.getResponse().getHeaders().getContentType();
                    log.info("Request [{}] response content-type is {}", exchange.getRequest().getPath().pathWithinApplication()
                            .value(), responseMediaType);
                    return MediaType.APPLICATION_JSON.isCompatibleWith(responseMediaType) ?
                            rewriteBody(exchange, originalBody) : Mono.just(originalBody);
                })).build();
        // 响应web前端
        return chain.filter(newExchange);
    }

    /**
     * 重写响应体
     *
     * @param exchange     ServerWebExchange
     * @param originalBody byte[]
     * @return Mono<byte [ ]>
     */
    private Mono<byte[]> rewriteBody(ServerWebExchange exchange, byte[] originalBody) {
        int originalResponseStatus = Objects.requireNonNull(exchange.getResponse().getStatusCode()).value();
        // 将状态码统一重置为200，在这里重置才是终极解决办法
        log.debug("Response status code is {} , body is {}", originalResponseStatus, new String(originalBody));
        JSONObject res = (JSONObject) JSON.parse(originalBody);
        // 下游服务响应内容为空，但是http状态码为200，则按照成功的响应体包装返回
        if (originalResponseStatus == HttpStatus.OK.value()) {
            if (originalBody.length == 0) {
                return Mono.just(JSON.toJSONBytes(CommonResult.success()));
            }
            try {
                // 只能parse出JSONObject、JSONArray、Integer、Boolean等类型，当是一个string但是非json格式则抛出异常
                res = (JSONObject) JSON.parse(originalBody);
                //如果响应内容已经包含了message字段，则表示下游的响应体本身已经是统一结果体了，无需再包装
                return Mono.just(res.containsKey(MESSAGE) ? originalBody : JSON.toJSONBytes(CommonResult.success()));
            } catch (Exception e) {
                // 响应结果是非json格式：包装错误返回
                return Mono.just(JSON.toJSONBytes(CommonResult.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Please check the backend response data format!")));
            }
        }
        // 401
        if (originalResponseStatus == HttpStatus.UNAUTHORIZED.value()) {
            return Mono.just(JSON.toJSONBytes(CommonResult.fail(originalResponseStatus, HttpStatus.UNAUTHORIZED.getReasonPhrase())));
        }
        // 403
        if (originalResponseStatus == HttpStatus.FORBIDDEN.value()) {
            return Mono.just(JSON.toJSONBytes(CommonResult.fail(originalResponseStatus, HttpStatus.FORBIDDEN.getReasonPhrase())));
        }
        // 400
        if (originalResponseStatus == HttpStatus.BAD_REQUEST.value()) {
            return Mono.just(JSON.toJSONBytes(CommonResult.fail(HttpStatus.BAD_REQUEST.value(), res.get("error").toString())));
        }
        // 其他状态码
        return Mono.just(JSON.toJSONBytes(CommonResult.fail(originalResponseStatus, res.get("message").toString(), "")));
    }

    /**
     * 是否需要过滤
     */
    public static final String IS_IGNORE_AUTH_FILTER = "ignore";

    /**
     * 响应体message字段标记
     */
    public static final String MESSAGE = "message";
}