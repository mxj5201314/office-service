package com.zhouzhao.office.online_collaborative_office.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhouzhao.office.online_collaborative_office.entity.TbHolidays;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 节假日表(TbHolidays)表数据库访问层
 *
 * @author makejava
 * @since 2022-04-18 00:10:40
 */

@Mapper
public interface TbHolidaysDao extends BaseMapper<TbHolidays> {

    Integer searchTodayIsHolidays();

    List<String> getHolidaysInRange(String startDate, String endDate);


}

