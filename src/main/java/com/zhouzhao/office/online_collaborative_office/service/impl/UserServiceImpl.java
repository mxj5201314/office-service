package com.zhouzhao.office.online_collaborative_office.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhouzhao.office.online_collaborative_office.common.Exception.GlobalException;
import com.zhouzhao.office.online_collaborative_office.common.components.RedisHandler;
import com.zhouzhao.office.online_collaborative_office.common.config.shiro.JwtUtil;
import com.zhouzhao.office.online_collaborative_office.common.constants.JwtConstant;
import com.zhouzhao.office.online_collaborative_office.common.enums.RespCodeEnum;
import com.zhouzhao.office.online_collaborative_office.common.utils.WXUtil;
import com.zhouzhao.office.online_collaborative_office.dao.TbDeptDao;
import com.zhouzhao.office.online_collaborative_office.dao.TbUserDao;
import com.zhouzhao.office.online_collaborative_office.dto.LoginDTO;
import com.zhouzhao.office.online_collaborative_office.dto.UserSummaryDTO;
import com.zhouzhao.office.online_collaborative_office.entity.TbDept;
import com.zhouzhao.office.online_collaborative_office.entity.TbUser;
import com.zhouzhao.office.online_collaborative_office.service.UserService;
import com.zhouzhao.office.online_collaborative_office.task.MessageTask;
import com.zhouzhao.office.online_collaborative_office.vo.LoginVO;
import com.zhouzhao.office.online_collaborative_office.vo.RegisterVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;
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

    @Autowired
    private TbDeptDao deptDao;


    @Override
    public LoginDTO registerUser(RegisterVO registerVO) throws GlobalException {
        Integer userId = null;
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
        Integer userid = userDao.getIdByOpenId(openId);
        if (userid == null) {
            throw new GlobalException(RespCodeEnum.ERR_USER_EXIST);
        }
        String token = jwtUtil.createToken(userid);
        setTokenCache(token, userid, cacheExpire, initTimeUnit);
        token = JwtConstant.TOKEN_HEAD + token;
        Set<String> userPermissions = userDao.getUserPermissions(userid);
        return new LoginDTO().setToken(token).setPermissions(userPermissions);
    }

    @Override
    public TbUser getUserById(Integer userId) {
        return userDao.getUserById(userId);
    }

    @Override
    public Set<String> getUserPermissions(Integer userid) {
        return userDao.getUserPermissions(userid);
    }


    private void setTokenCache(String token, Integer userId, Long expireTime, TimeUnit timeUnit) throws GlobalException {
        redisHandler.setString(token, userId + "", expireTime, timeUnit);
    }

    @Override
    public String getUserHiredate(Integer userId) {

        return userDao.getUserHiredate(userId);
    }

    @Override
    public List<TbDept> getUserGroupByDept(String keyword) {

        List<TbDept> list_1 = deptDao.getDeptMembers(keyword);
        List<HashMap<String, Object>> list_2 = userDao.getUserGroupByDept(keyword);
        for (TbDept tbDept : list_1) {
            Integer deptId = tbDept.getId();
            ArrayList members = new ArrayList();
            for (HashMap map : list_2) {
                Long deptId1 = (Long)map.get("deptId");
                if (deptId1.equals(deptId.longValue())) {
                    members.add(map);
                }
            }
            tbDept.setMembers(members);
        }
        return list_1;

    }

    @Override
    public String getMemberEmail(Integer id) {
        String email = userDao.getMemberEmail(id);
        return email;
    }

    @Override
    public List<UserSummaryDTO> getUserPhotoAndName(List<Integer> param) {
        List<TbUser> members = userDao.getUserPhotoAndName(param);
        List<UserSummaryDTO> userSummaryDTOS = new ArrayList<>();
        members.forEach(item -> {
            UserSummaryDTO userSummaryDTO = new UserSummaryDTO();
            BeanUtils.copyProperties(item, userSummaryDTO);
            userSummaryDTOS.add(userSummaryDTO);
        });

        return userSummaryDTOS;
    }

    @Override
    public List<UserSummaryDTO> getMembers(List<Integer> param) {
        List<TbUser> members = userDao.getMembers(param);
        List<UserSummaryDTO> userSummaryDTOS = new ArrayList<>();
        members.forEach(item -> {
            UserSummaryDTO userSummaryDTO = new UserSummaryDTO();
            BeanUtils.copyProperties(item, userSummaryDTO);
            userSummaryDTOS.add(userSummaryDTO);
        });

        return userSummaryDTOS;
    }
}

