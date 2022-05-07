package com.zhouzhao.office.online_collaborative_office.service;

import com.zhouzhao.office.online_collaborative_office.common.Exception.GlobalException;
import com.zhouzhao.office.online_collaborative_office.entity.TbMeeting;

import java.util.ArrayList;

public interface MeetingService {
    void insertMeeting(TbMeeting entity) throws GlobalException;
    ArrayList<TbMeeting> getMyMeetingListByPage(String userId,int start,int length);

}
