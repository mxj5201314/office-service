package com.zhouzhao.office.online_collaborative_office.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhouzhao.office.online_collaborative_office.entity.TbCheckin;
import com.zhouzhao.office.online_collaborative_office.vo.ValidCheckinVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 签到表(TbCheckin)表数据库访问层
 *
 * @author makejava
 * @since 2022-04-18 00:43:36
 */
@Mapper
public interface TbCheckinDao extends BaseMapper<TbCheckin> {
    Integer haveCheckIn(ValidCheckinVO validCheckinVO);

    Integer getCheckinTotalByUserId(Integer userId);

    List<TbCheckin> getWeekCheckin(Integer userId, String startDate, String endDate);
}

