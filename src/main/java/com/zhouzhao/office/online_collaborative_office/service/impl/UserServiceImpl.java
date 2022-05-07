package com.zhouzhao.office.online_collaborative_office.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhouzhao.office.online_collaborative_office.common.Exception.GlobalException;
import com.zhouzhao.office.online_collaborative_office.common.components.RedisHandler;
import com.zhouzhao.office.online_collaborative_office.common.config.shiro.JwtUtil;
import com.zhouzhao.office.online_collaborative_office.common.constants.JwtConstant;
import com.zhouzhao.office.online_collaborative_office.common.enums.RespCodeEnum;
import com.zhouzhao.office.online_collaborative_office.common.utils.WXUtil;
import com.zhouzhao.office.online_collaborative_office.dao.TbUserDao;
import com.zhouzhao.office.online_collaborative_office.dto.LoginDTO;
import com.zhouzhao.office.online_collaborative_office.entity.TbUser;
import com.zhouzhao.office.online_collaborative_office.service.UserService;
import com.zhouzhao.office.online_collaborative_office.task.MessageTask;
import com.zhouzhao.office.online_collaborative_office.vo.LoginVO;
import com.zhouzhao.office.online_collaborative_office.vo.RegisterVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 用户表(TbUser)表服务实现类
 *
 * @author makejava
 * @since 2022-03-21 18:10:06
 */
@Service("tbUserService")
@Slf4j
@Scope("prototype")
public class UserServiceImpl extends ServiceImpl<TbUserDao, TbUser> implements UserService {

    @Value("${wx.app-id}")
    private String appId;

    @Value("${wx.app-secret}")
    private String appSecret;

    @Value("${office.administrator.code}")
    private String administratorCode;

    @Value("${office.jwt.cache-expire}")
    private Long cacheExpire;

    private TimeUnit initTimeUnit = TimeUnit.DAYS;

    @Autowired
    private TbUserDao userDao;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisHandler redisHandler;

    @Autowired
    private MessageTask messageTask;


    @Override
    public LoginDTO registerUser(RegisterVO registerVO) throws GlobalException {
        String userId = null;
        Set<String> userPermissions = null;
        //判断是否注册超级管理员
        if (administratorCode.equals(registerVO.getRegisterCode())) {
            //查询超级管理员帐户是否已经绑定
            boolean bool = userDao.haveRootUser();
            if (!bool) {
                //把当前用户绑定到ROOT帐户
                String openId = WXUtil.getOpenId(appId, appSecret, registerVO.getCode());
                if (StringUtils.isBlank(openId)) {
                    throw new GlobalException(RespCodeEnum.ERR_NOT_GRT_OPENID);
                }
                TbUser tbUser = new TbUser().setOpenId(openId)
                        .setNickname(registerVO.getNickname())
                        .setPhoto(registerVO.getPhoto())
                        .setRole("[0]")
                        .setStatus(1)
                        .setCreateTime(new Date()).setRoot(true);

                int insert = userDao.insert(tbUser);
                if (insert < 0) {
                    throw new GlobalException(RespCodeEnum.FAIL);
                }

                userId = tbUser.getId();
                userPermissions = userDao.getUserPermissions(userId);
            } else {
                //如果root已经绑定了，就抛出异常
                throw new GlobalException(RespCodeEnum.ERR_ADMINISTRATOR_EXIST);
            }
        } else {
            return null;
        }
        String token = JwtConstant.TOKEN_HEAD + jwtUtil.createToken(userId);
        setTokenCache(token, userId, cacheExpire, initTimeUnit);
        return new LoginDTO().setToken(token).setPermissions(userPermissions);
    }

    @Override
    public LoginDTO login(LoginVO loginVO) throws GlobalException {
        String openId = WXUtil.getOpenId(appId, appSecret, loginVO.getCode());
        if (StringUtils.isBlank(openId)) {
            throw new GlobalException(RespCodeEnum.ERR_NOT_GRT_OPENID);
        }
        String userid = userDao.getIdByOpenId(openId);
        if (StringUtils.isBlank(userid)) {
            throw new GlobalException(RespCodeEnum.ERR_USER_EXIST);
        }
        String token = jwtUtil.createToken(userid);
        setTokenCache(token, userid, cacheExpire, initTimeUnit);
        token = JwtConstant.TOKEN_HEAD + token;
        Set<String> userPermissions = userDao.getUserPermissions(userid);
        return new LoginDTO().setToken(token).setPermissions(userPermissions);
    }

    @Override
    public TbUser getUserById(String userId) {
        return userDao.getUserById(userId);
    }

    @Override
    public Set<String> getUserPermissions(String userid) {
        return userDao.getUserPermissions(userid);
    }


    private void setTokenCache(String token, String userId, Long expireTime, TimeUnit timeUnit) throws GlobalException {
        redisHandler.setString(token, userId, expireTime, timeUnit);
    }

    @Override
    public String getUserHiredate(String userId) {

        return userDao.getUserHiredate(userId);
    }
}

