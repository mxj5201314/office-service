package com.zhouzhao.office.online_collaborative_office.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhouzhao.office.online_collaborative_office.entity.TbWorkday;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * (TbWorkday)表数据库访问层
 *
 * @author makejava
 * @since 2022-04-18 00:12:58
 */

@Mapper
public interface TbWorkdayDao extends BaseMapper<TbWorkday> {
    Integer searchTodayIsWorkday();

    List<String> getWorkdayInRange(String startDate,String endDate);
}

