package com.zhouzhao.office.online_collaborative_office.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhouzhao.office.online_collaborative_office.entity.TbMeeting;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 会议表(TbMeeting)表数据库访问层
 *
 * @author makejava
 * @since 2022-05-07 11:30:55
 */

@Mapper
public interface TbMeetingDao extends BaseMapper<TbMeeting> {
    int insertMeeting(TbMeeting entity);

    ArrayList<TbMeeting> getMyMeetingListByPage(Integer userId, int start, int length);

    boolean getMeetingMembersInSameDept(String uuid);

    int updateMeetingInstanceId(String uuid, String instanceId);

    TbMeeting getMeetingById(Integer id);

    int updateMeetingInfo(TbMeeting tbMeeting);

    int deleteMeetingById(Integer id);

    List<String> getUserMeetingInMonth(HashMap param);
}

