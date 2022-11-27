package com.aurora.gateway.exception;

import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.function.Function;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR;

/**
 * @Description ServerHttpResponseDecoratorHelper工具类消除模板代码
 * 在读取或修改response body时必须用到ServerHttpResponseDecorator
 * @Date 2022-11-27 1:23
 * @Version v1.0
 * @Author wh14787946648@outlook.com
 **/
public class ServerHttpResponseDecoratorHelper {
    public static ServerHttpResponseDecorator build(ServerWebExchange exchange, MyRewriteFunction<byte[], byte[]>
            rewriteFunction) {
        return new ServerHttpResponseDecorator(exchange.getResponse()) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                String originalResponseContentType = exchange.getAttribute(ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR);
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add(HttpHeaders.CONTENT_TYPE, originalResponseContentType);
                ClientResponse clientResponse = prepareClientResponse(body, httpHeaders);
                if (!MediaType.APPLICATION_JSON.isCompatibleWith(httpHeaders.getContentType())) {
                    return super.writeWith(body);
                }
                //  flux or mono
                Mono<byte[]> modifiedBody = clientResponse.bodyToMono(byte[].class).flatMap(rewriteFunction);
                return bodyInsert(this, exchange, modifiedBody);
            }

            @Override
            public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
                return writeWith(Flux.from(body).flatMapSequential(p -> p));
            }

            private ClientResponse prepareClientResponse(
                    Publisher<? extends DataBuffer> body, HttpHeaders httpHeaders) {
                ClientResponse.Builder builder;
                builder = ClientResponse.create(Objects.requireNonNull(exchange.getResponse().getStatusCode()));
                return builder.headers(headers -> headers.putAll(httpHeaders)).body(Flux.from(body)).build();
            }
        };
    }

    public static Mono<Void> bodyInsert(ServerHttpResponseDecorator decorator,
                                        ServerWebExchange exchange,
                                        Mono<byte[]> modifiedBody) {
        BodyInserter<Mono<byte[]>, ReactiveHttpOutputMessage> bodyInserter = BodyInserters.fromPublisher(modifiedBody,
                byte[].class);
        CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, exchange.getResponse().getHeaders());
        return bodyInserter.insert(outputMessage, new BodyInserterContext()).then(Mono.defer(() -> {
            Flux<DataBuffer> messageBody = outputMessage.getBody();
            HttpHeaders headers = decorator.getDelegate().getHeaders();
            if (!headers.containsKey(HttpHeaders.TRANSFER_ENCODING)) {
                messageBody = messageBody.doOnNext(data -> headers
                        .setContentLength(data.readableByteCount()));
            }
            return decorator.getDelegate().writeWith(messageBody);
        }));
    }

    public interface MyRewriteFunction<T, R>
            extends Function<T, Mono<R>> {

    }
}

