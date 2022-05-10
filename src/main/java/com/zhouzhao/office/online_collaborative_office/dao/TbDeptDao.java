package com.zhouzhao.office.online_collaborative_office.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhouzhao.office.online_collaborative_office.entity.TbDept;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * (TbDept)表数据库访问层
 *
 * @author makejava
 * @since 2022-05-07 20:50:47
 */
@Mapper
public interface TbDeptDao extends BaseMapper<TbDept> {
    List<TbDept> getDeptMembers(String keyword);

}

