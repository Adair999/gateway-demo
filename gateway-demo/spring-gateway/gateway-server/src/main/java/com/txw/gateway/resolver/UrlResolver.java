package com.txw.gateway.resolver;


import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
/**
 * 限流配置
 */
@Slf4j
@Component
public class UrlResolver implements KeyResolver {
    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        // String ip=exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        // 获取请求地址
        String url = exchange.getRequest().getURI().toString();
        log.info("限流的url:{}", url);
        return Mono.just(url);
    }
}