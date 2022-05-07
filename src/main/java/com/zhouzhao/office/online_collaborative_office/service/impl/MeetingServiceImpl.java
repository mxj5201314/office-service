package com.zhouzhao.office.online_collaborative_office.service.impl;

import cn.hutool.json.JSONArray;
import com.zhouzhao.office.online_collaborative_office.common.Exception.GlobalException;
import com.zhouzhao.office.online_collaborative_office.common.enums.RespCodeEnum;
import com.zhouzhao.office.online_collaborative_office.dao.TbMeetingDao;
import com.zhouzhao.office.online_collaborative_office.entity.TbMeeting;
import com.zhouzhao.office.online_collaborative_office.service.MeetingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;


@Slf4j
@Service
public class MeetingServiceImpl implements MeetingService {

    @Autowired
    private TbMeetingDao meetingDao;

    @Override
    public void insertMeeting(TbMeeting entity) throws GlobalException {
        //保存数据
        int row = meetingDao.insertMeeting(entity);
        if (row <= 0) {
            throw new GlobalException(RespCodeEnum.ERR_ADD_MEETING);
        }
        //TODO 开启审批工作流

    }

    @Override
    public ArrayList<TbMeeting> getMyMeetingListByPage(String userId,int start,int length) {
        ArrayList<TbMeeting> list = meetingDao.getMyMeetingListByPage(userId,start,length);
        String date = null;
        ArrayList resultList = new ArrayList();
        HashMap resultMap;
        JSONArray array = null;
        for (TbMeeting tbMeeting : list) {
            String temp = tbMeeting.getDate();
            if (!temp.equals(date)) {
                date = temp;
                resultMap = new HashMap();
                resultList.add(resultMap);
                resultMap.put("date", date);
                array = new JSONArray();
                resultMap.put("list", array);
            }
            array.put(tbMeeting);
        }
        return resultList;
    }

}
