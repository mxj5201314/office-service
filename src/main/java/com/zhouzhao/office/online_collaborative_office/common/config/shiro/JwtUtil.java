package com.zhouzhao.office.online_collaborative_office.common.config.shiro;


import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${office.jwt.secret}")
    private String secret;
    @Value("${office.jwt.expire}")
    private String expire;


    public  String createToken(String userId) {
        DateTime dateTime = DateUtil.offsetDay(new Date(), Integer.parseInt(expire));
        return JWT.create()
                .withClaim("userId", userId)//存放数据
                .withExpiresAt(dateTime)
                .sign(Algorithm.HMAC256(secret));
    }

    public String getUserId(String token) {
        System.out.println("jwtUtil-token = " + token);
        return JWT.decode(token).getClaim("userId").asString();
    }

    public void verifyToken(String token) {
        JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
    }


}
