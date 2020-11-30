package com.txw.gateway.config;


        import com.alibaba.fastjson.JSON;
        import com.txw.gateway.dto.CommonResult;
        import lombok.extern.slf4j.Slf4j;
        import org.apache.commons.lang3.StringUtils;
        import org.springframework.cloud.gateway.filter.GatewayFilterChain;
        import org.springframework.cloud.gateway.filter.GlobalFilter;
        import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
        import org.springframework.cloud.gateway.support.BodyInserterContext;
        import org.springframework.cloud.gateway.support.DefaultServerRequest;
        import org.springframework.core.Ordered;
        import org.springframework.core.io.buffer.DataBuffer;
        import org.springframework.http.HttpHeaders;
        import org.springframework.http.HttpStatus;
        import org.springframework.http.MediaType;
        import org.springframework.http.server.reactive.ServerHttpRequest;
        import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
        import org.springframework.http.server.reactive.ServerHttpResponse;
        import org.springframework.stereotype.Component;
        import org.springframework.web.reactive.function.BodyInserter;
        import org.springframework.web.reactive.function.BodyInserters;
        import org.springframework.web.reactive.function.server.ServerRequest;
        import org.springframework.web.server.ServerWebExchange;
        import reactor.core.publisher.Flux;
        import reactor.core.publisher.Mono;
        import java.io.UnsupportedEncodingException;
        import java.util.Arrays;
/**
 * 转发之前拦截 白名单过滤 鉴权
 */
@Component
@Slf4j
public class TokenFilter implements GlobalFilter, Ordered {
    // 白名单 排除无需验证的 token
    private static final String[] whiteList = {"/auth/login", "/auth/logout", "/auth/logout", "/auth/kaptcha"};
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        String url = serverHttpRequest.getURI().getPath();
        log.info("获取请求的url：{}", url);
        if (Arrays.asList(whiteList).contains(url)) {
            log.info("白名单url：" + url + "，直接放行");
            return chain.filter(exchange);
        }
        String token = serverHttpRequest.getQueryParams().getFirst(AppConstants.TOKEN);
        log.info("获取请求的token：{}", token);
        if (StringUtils.isBlank(token)) {
            return setResponse(exchange, "鉴权失败，access-token为空");
        }
        //JWT，redis
        return chain.filter(exchange);
    }
    /**
     * 设置 拦截返回信息
     *
     * @param exchange
     * @param msg
     * @return
     */
    private Mono<Void> setResponse(ServerWebExchange exchange, String msg) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        originalResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
        originalResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        byte[] response = null;
        try {
            //log.info("token已失效");
            response = JSON.toJSONString(CommonResult.error(msg, "")).getBytes(AppConstants.UTF8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        DataBuffer buffer = originalResponse.bufferFactory().wrap(response);
        return originalResponse.writeWith(Flux.just(buffer));
    }
    /**
     * 修改body参数 post json
     *
     * @param exchange
     * @param chain
     * @param newBody
     * @return
     */
    private Mono<Void> operationExchange(ServerWebExchange exchange, GatewayFilterChain chain, String newBody) {
        // mediaType
        MediaType mediaType = exchange.getRequest().getHeaders().getContentType();
        // read & modify body
        ServerRequest serverRequest = new DefaultServerRequest(exchange);
        Mono<String> modifiedBody = serverRequest.bodyToMono(String.class)
                .flatMap(body -> {
                    if (MediaType.APPLICATION_JSON.isCompatibleWith(mediaType)) {
                        System.out.println(body);
                        log.info("原传入参数:" + body);
                        // 对原先的body进行修改操作 如加解密操作
                        //String newBody = "{\"testName\":\"testValue\"}";
                        return Mono.just(newBody);
                    }
                    return Mono.empty();
                });
        BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(exchange.getRequest().getHeaders());
        headers.remove(HttpHeaders.CONTENT_LENGTH);
        CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);
        return bodyInserter.insert(outputMessage, new BodyInserterContext())
                .then(Mono.defer(() -> {
                    ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(
                            exchange.getRequest()) {
                        @Override
                        public HttpHeaders getHeaders() {
                            long contentLength = headers.getContentLength();
                            HttpHeaders httpHeaders = new HttpHeaders();
                            httpHeaders.putAll(super.getHeaders());
                            if (contentLength > 0) {
                                httpHeaders.setContentLength(contentLength);
                            } else {
                                httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                            }
                            return httpHeaders;
                        }

                        @Override
                        public Flux<DataBuffer> getBody() {
                            return outputMessage.getBody();
                        }
                    };
                    return chain.filter(exchange.mutate().request(decorator).build());
                }));
    }
    @Override
    public int getOrder() {
        return -200;
    }
}