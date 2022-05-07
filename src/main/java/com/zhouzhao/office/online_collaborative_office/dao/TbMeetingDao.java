package com.zhouzhao.office.online_collaborative_office.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhouzhao.office.online_collaborative_office.entity.TbMeeting;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

/**
 * 会议表(TbMeeting)表数据库访问层
 *
 * @author makejava
 * @since 2022-05-07 11:30:55
 */

@Mapper
public interface TbMeetingDao extends BaseMapper<TbMeeting> {
    int insertMeeting(TbMeeting entity);
    ArrayList<TbMeeting> getMyMeetingListByPage(String userId,int start,int length);
}

