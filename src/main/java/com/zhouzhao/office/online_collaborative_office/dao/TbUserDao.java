package com.zhouzhao.office.online_collaborative_office.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhouzhao.office.online_collaborative_office.entity.TbUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.Set;

/**
 * 用户表(TbUser)表数据库访问层
 *
 * @author makejava
 * @since 2022-03-21 18:10:04
 */
@Mapper
public interface TbUserDao extends BaseMapper<TbUser> {


    Boolean haveRootUser();

    Set<String> getUserPermissions(String userId);

    String getIdByOpenId(String openId);

    TbUser getNameAndDeptAndTel(String userId);

    TbUser getUserById(String userId);

    String getUserHiredate(String userId);

}

