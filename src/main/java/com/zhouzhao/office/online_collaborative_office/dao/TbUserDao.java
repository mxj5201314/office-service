package com.zhouzhao.office.online_collaborative_office.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhouzhao.office.online_collaborative_office.entity.TbUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;
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

    Set<String> getUserPermissions(Integer userId);

    Integer getIdByOpenId(String openId);

    TbUser getNameAndDeptAndTel(Integer userId);

    TbUser getUserById(Integer userId);

    String getUserHiredate(Integer userId);

    List<HashMap<String, Object>> getUserGroupByDept(String keyword);

    List<TbUser> getMembers(List<Integer> param);

    TbUser getUserInfo(Integer userId);

    String getDeptManagerId(Integer id);

    Integer getGmId();

    List<TbUser> getMeetingMembers(Integer id);

    List<TbUser> getUserPhotoAndName(List<Integer> param);

    String getMemberEmail(Integer id);
}

