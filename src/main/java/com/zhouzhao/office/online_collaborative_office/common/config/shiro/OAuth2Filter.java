package com.zhouzhao.office.online_collaborative_office.common.config.shiro;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.zhouzhao.office.online_collaborative_office.common.components.RedisHandler;
import com.zhouzhao.office.online_collaborative_office.common.constants.JwtConstant;
import com.zhouzhao.office.online_collaborative_office.common.enums.RespCodeEnum;
import com.zhouzhao.office.online_collaborative_office.common.utils.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@Scope("prototype")
@Slf4j
public class OAuth2Filter extends AuthenticatingFilter {

    @Autowired
    private ThreadLocalToken threadLocalToken;


    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisHandler redisHandler;

    @Value("${office.jwt.cache-expire}")
    private Long cacheExpire;

    TimeUnit initTimeUnit = TimeUnit.DAYS;

    /**
     * 拦截请求之后，用于把令牌字符串封装成令牌对象
     */
    @Override
    protected AuthenticationToken createToken(ServletRequest request,
                                              ServletResponse response) throws Exception {
        //获取请求token
        String token = getRequestToken((HttpServletRequest) request);
        if (StringUtils.isBlank(token)) {
            return null;
        }
        return new OAuth2Token(token);
    }

    /**
     * 拦截请求，判断请求是否需要被Shiro处理
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request,
                                      ServletResponse response, Object mappedValue) {
        HttpServletRequest req = (HttpServletRequest) request;
        // Ajax提交application/json数据的时候，会先发出Options请求
        // 这里要放行Options请求，不需要Shiro处理
        return req.getMethod().equals(RequestMethod.OPTIONS.name());
        // 除了Options请求之外，所有请求都要被Shiro处理
    }

    /**
     * 该方法用于处理所有应该被Shiro处理的请求
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request,
                                     ServletResponse response) throws Exception {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setHeader("Content-Type", "application/json;charset=utf-8");
        //允许跨域请求
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
        threadLocalToken.clearToken();
        //获取请求token，如果token不存在，直接返回401
        String token = getRequestToken((HttpServletRequest) request);
        log.error("令牌为：{}", token);
        if (StringUtils.isBlank(token)) {
            String s = JSON.toJSONString(BaseResponse.fail(RespCodeEnum.ERR_TOKEN_INVALID));
            resp.getWriter().print(s);
            return false;
        }
        try {
            jwtUtil.verifyToken(token); //检查令牌是否过期,过期会抛出异常
        } catch (TokenExpiredException e) {
            //客户端令牌过期，查询Redis中是否存在令牌，如果存在令牌就重新生成一个令牌给客户端
            if (redisHandler.exists(token)) {
                redisHandler.remove(token);
                String userId = jwtUtil.getUserId(token);
                token = jwtUtil.createToken(userId); //生成新的令牌
                //把新的令牌保存到Redis中
                redisHandler.setString(token, userId, cacheExpire, initTimeUnit);
                //把新令牌绑定到线程
                threadLocalToken.setToken(token);
            } else {
                //如果Redis不存在令牌，让用户重新登录
                String s = JSON.toJSONString(BaseResponse.fail(RespCodeEnum.ERR_TOKEN_EXPIRE));
                resp.getWriter().print(s);
                return false;
            }
        } catch (JWTDecodeException e) {
            String s = JSON.toJSONString(BaseResponse.fail(RespCodeEnum.ERR_TOKEN_EXPIRE));
            resp.getWriter().print(s);
            return false;
        }
        return executeLogin(request, response);
    }

    @Override
    protected boolean onLoginFailure(AuthenticationToken token,
                                     AuthenticationException e, ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setContentType("application/json;charset=utf-8");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
        String s = JSON.toJSONString(BaseResponse.fail(e.getMessage()));
        try {
            resp.getWriter().print(s);
        } catch (IOException es) {
            log.error(es.getMessage());
        }
        return false;
    }

    /**
     * 获取请求头里面的token
     */
    private String getRequestToken(HttpServletRequest httpRequest) {
        //从header中获取token
        String token = httpRequest.getHeader(JwtConstant.HTTP_HEADER_NAME);
        //如果header中不存在token，则从参数中获取token
        if (StringUtils.isBlank(token)) {
            token = httpRequest.getParameter(JwtConstant.HTTP_HEADER_NAME);
        }
        if (StringUtils.isNotBlank(token) && token.startsWith(JwtConstant.TOKEN_HEAD)) {
            token = token.substring(JwtConstant.TOKEN_HEAD.length());
        }
        return token;
    }

    @Override
    public void doFilterInternal(ServletRequest request,
                                 ServletResponse response, FilterChain chain) throws ServletException, IOException {
        super.doFilterInternal(request, response, chain);
    }
}
