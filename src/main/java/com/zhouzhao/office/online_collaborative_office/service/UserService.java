package com.zhouzhao.office.online_collaborative_office.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhouzhao.office.online_collaborative_office.common.Exception.GlobalException;
import com.zhouzhao.office.online_collaborative_office.dto.LoginDTO;
import com.zhouzhao.office.online_collaborative_office.entity.TbUser;
import com.zhouzhao.office.online_collaborative_office.vo.LoginVO;
import com.zhouzhao.office.online_collaborative_office.vo.RegisterVO;

import java.util.Set;

/**
 * 用户表(TbUser)表服务接口
 *
 * @author makejava
 * @since 2022-03-21 18:10:06
 */
public interface UserService extends IService<TbUser> {
    LoginDTO registerUser(RegisterVO registerVO) throws GlobalException;

    LoginDTO login(LoginVO loginVO) throws GlobalException;

    TbUser getUserById(String token);

    Set<String> getUserPermissions(String userid);

    String getUserHiredate(String userId);
}

