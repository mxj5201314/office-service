package com.zhouzhao.office.online_collaborative_office.service;

import com.zhouzhao.office.online_collaborative_office.common.Exception.GlobalException;
import com.zhouzhao.office.online_collaborative_office.dto.MeetingInfoDTO;
import com.zhouzhao.office.online_collaborative_office.entity.TbMeeting;
import com.zhouzhao.office.online_collaborative_office.vo.UpdateMeetingVO;

import java.util.HashMap;
import java.util.List;

public interface MeetingService {
    void insertMeeting(TbMeeting entity) throws GlobalException;
    List<TbMeeting> getMyMeetingListByPage(Integer userId, int start, int length);

    MeetingInfoDTO getMeetingById(Integer id);

    void updateMeetingInfo(UpdateMeetingVO vo) throws GlobalException;

    void deleteMeetingById(Integer id) throws GlobalException;

    Long getRoomIdByUUID(String uuid);

    List<String> getUserMeetingInMonth(HashMap param);
}
