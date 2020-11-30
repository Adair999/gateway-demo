package com.txw.zuul.config;

import com.google.common.util.concurrent.RateLimiter;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
@Component
public class RateLimiterFilter extends ZuulFilter {
    // 每秒钟产生5个令牌，如果没有拿到令牌，就拒绝访问
    private static  final RateLimiter RATE_LIMITER = RateLimiter.create(5);
    @Override
    public String filterType() {
        return "pre";
    }
    @Override
    public int filterOrder() {
        return 2;
    }
    @Override
    public boolean shouldFilter() {
        // 针对某些地址做限流
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        if (request.getRequestURI().equalsIgnoreCase("/api-storage/storage")) {
            return true;
        }
        return false;
    }
    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        if(!RATE_LIMITER.tryAcquire()){
            // 没有token，鉴权校验失败，拦截
            context.setSendZuulResponse(false);
            context.getResponse().setContentType("text/html;charset=UTF-8");
            context.setResponseBody("当前访问量过大，请稍后重试");
            context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
            System.out.println("当前访问量过大，请稍后重试");
        }
        return null;
    }
}