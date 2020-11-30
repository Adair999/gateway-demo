package com.txw.zuul.config;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
@Component
public class LoginFilter extends ZuulFilter {
    @Override
    public String filterType() {
        // 登录校验，肯定是在前置拦截
        return "pre";
    }
    @Override
    public int filterOrder() {
        // 顺序设置为1
        return 1;
    }
    @Override
    public boolean shouldFilter() {
        // true表示过滤器生效
        return true;
    }
    @Override
    public Object run() throws ZuulException {
        // 获取上下文
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest req = context.getRequest();
        String token = req.getParameter("access-token");
        if (token == null || "".equals(token.trim())) {
            // 没有token，鉴权校验失败，拦截
            context.setSendZuulResponse(false);
            context.getResponse().setContentType("text/html;charset=UTF-8");
            context.setResponseBody("鉴权失败，access-token为空");
            context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        }
        return null;
    }
}