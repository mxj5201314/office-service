package com.zhouzhao.office.online_collaborative_office.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhouzhao.office.online_collaborative_office.entity.SysConfig;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * (SysConfig)表数据库访问层
 *
 * @author makejava
 * @since 2022-04-09 16:35:34
 */
@Mapper
public interface SysConfigDao extends BaseMapper<SysConfig> {
    List<SysConfig> selectAllParam();
}

