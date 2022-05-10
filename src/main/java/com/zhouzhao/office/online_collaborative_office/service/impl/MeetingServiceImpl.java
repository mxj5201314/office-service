package com.zhouzhao.office.online_collaborative_office.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zhouzhao.office.online_collaborative_office.common.Exception.GlobalException;
import com.zhouzhao.office.online_collaborative_office.common.components.RedisHandler;
import com.zhouzhao.office.online_collaborative_office.common.enums.RespCodeEnum;
import com.zhouzhao.office.online_collaborative_office.dao.TbMeetingDao;
import com.zhouzhao.office.online_collaborative_office.dao.TbUserDao;
import com.zhouzhao.office.online_collaborative_office.dto.MeetingInfoDTO;
import com.zhouzhao.office.online_collaborative_office.entity.TbMeeting;
import com.zhouzhao.office.online_collaborative_office.entity.TbUser;
import com.zhouzhao.office.online_collaborative_office.service.MeetingService;
import com.zhouzhao.office.online_collaborative_office.vo.UpdateMeetingVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Slf4j
@Service
public class MeetingServiceImpl implements MeetingService {

    @Autowired
    private TbMeetingDao meetingDao;

    @Autowired
    private TbUserDao userDao;

    @Value("${office.code}")
    private String code;

    @Value("${workflow.url}")
    private String workflow;

    @Value("${office.recieveNotify}")
    private String recieveNotify;

    @Autowired
    RedisHandler redisHandler;

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    @Transactional
    public void insertMeeting(TbMeeting entity) throws GlobalException {
        //保存数据
        int row = meetingDao.insertMeeting(entity);
        if (row <= 0) {
            throw new GlobalException(RespCodeEnum.ERR_ADD_MEETING);
        }
        String meetingType = entity.getType() == 1 ? "线上会议" : "线下会议";
        startMeetingWorkflow(entity.getUuid(), entity.getCreatorId(), entity.getTitle(), entity.getDate(), entity.getStart(), meetingType);

    }

    @Override
    public ArrayList<TbMeeting> getMyMeetingListByPage(Integer userId, int start, int length) {
        ArrayList<TbMeeting> list = meetingDao.getMyMeetingListByPage(userId, start, length);
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

    @Override
    public MeetingInfoDTO getMeetingById(Integer id) {
        TbMeeting meetingById = meetingDao.getMeetingById(id);
        MeetingInfoDTO meetingInfoDTO = new MeetingInfoDTO();
        BeanUtils.copyProperties(meetingById, meetingInfoDTO);
        List<TbUser> meetingMembers = userDao.getMeetingMembers(id);
        meetingInfoDTO.setMembers(meetingMembers);
        return meetingInfoDTO;
    }

    @Override
    @Transactional
    public void updateMeetingInfo(UpdateMeetingVO vo) throws GlobalException {
        int id = vo.getId();
        String date = vo.getDate();
        String start = vo.getStart();
        String instanceId = vo.getInstanceId();

        //查询修改前的会议记录
        TbMeeting meetingById = meetingDao.getMeetingById(id);
        String uuid = meetingById.getUuid();
        Integer creatorId = meetingById.getCreatorId();

        TbMeeting tbMeeting = new TbMeeting();
        BeanUtils.copyProperties(vo, tbMeeting);
        int row = meetingDao.updateMeetingInfo(tbMeeting); //更新会议记录
        if (row != 1) {
            throw new GlobalException("会议更新失败");
        }

        //会议更新成功之后，删除以前的工作流
        JSONObject json = new JSONObject();
        json.set("instanceId", instanceId);
        json.set("reason", "会议被修改");
        json.set("uuid", uuid);
        json.set("code", code);
        String url = workflow + "/workflow/deleteProcessById";
        HttpResponse resp = HttpRequest.post(url).header("content-type", "application/json").body(json.toString()).execute();
        if (resp.getStatus() != 200) {
            log.error("删除工作流失败");
            throw new GlobalException(RespCodeEnum.FAIL.getCode(), "删除工作流失败");
        }
        String meetingType = vo.getType() == 1 ? "线上会议" : "线下会议";
        //创建新的工作流
        startMeetingWorkflow(uuid, creatorId, meetingById.getTitle(), date, start, meetingType);
    }

    @Override
    @Transactional
    public void deleteMeetingById(Integer id) throws GlobalException {
        TbMeeting meeting = meetingDao.getMeetingById(id);//查询会议信息
        String uuid = meeting.getUuid();
        String instanceId = meeting.getInstanceId();
        DateTime date = DateUtil.parse(meeting.getDate() + " " + meeting.getStart());
        DateTime now = DateUtil.date();
        //会议开始前20分钟，不能删除会议
        if (now.isAfterOrEquals(date.offset(DateField.MINUTE, -20))) {
            throw new GlobalException(RespCodeEnum.FAIL.getCode(), "距离会议开始不足20分钟，不能删除会议");
        }
        ;
        int row = meetingDao.deleteMeetingById(id);
        if (row != 1) {
            throw new GlobalException(RespCodeEnum.FAIL.getCode(), "会议删除失败");
        }

        //删除会议工作流
        JSONObject json = new JSONObject();
        json.set("instanceId", instanceId);
        json.set("reason", "会议被取消");
        json.set("code", code);
        json.set("uuid", uuid);
        String url = workflow + "/workflow/deleteProcessById";
        HttpResponse resp = HttpRequest.post(url).header("content-type", "application/json").body(json.toString()).execute();
        if (resp.getStatus() != 200) {
            log.error("删除工作流失败");
            throw new GlobalException(RespCodeEnum.FAIL.getCode(), "删除工作流失败");
        }
    }

    @Override
    public Long getRoomIdByUUID(String uuid) {
        //String string = redisHandler.getString(uuid);
        //long roomId = Long.parseLong(string);
        Object temp = redisTemplate.opsForValue().get(uuid);
        long roomId = Long.parseLong(temp.toString());
        return roomId;
    }

    @Override
    public List<String> getUserMeetingInMonth(HashMap param) {
        return meetingDao.getUserMeetingInMonth(param);
    }


    private void startMeetingWorkflow(String uuid, Integer creatorId, String title, String date, String start, String meetingType) throws GlobalException {
        TbUser userInfo = userDao.getUserInfo(creatorId);//查询创建者用户信息

        JSONObject json = new JSONObject();
        json.set("url", recieveNotify);
        json.set("uuid", uuid);
        //json.set("openId", userInfo.getOpenId());
        json.set("code", code);
        json.set("date", date);
        json.set("start", start);
        json.set("creatorId", creatorId);
        json.set("creatorName", userInfo.getName());
        json.set("title", title);
        json.set("meetingType", meetingType);
        String[] roles = userInfo.getRoles().split("，");
        //如果不是总经理创建的会议
        if (!ArrayUtil.contains(roles, "总经理")) {
            //查询总经理ID和同部门的经理的ID
            String deptManagerId = userDao.getDeptManagerId(creatorId);
            json.set("managerId", deptManagerId); //部门经理ID
            Integer gmId = userDao.getGmId();//总经理ID
            json.set("gmId", gmId);
            //查询会议员工是不是同一个部门
            boolean bool = meetingDao.getMeetingMembersInSameDept(uuid);
            json.set("sameDept", bool);
        }
        String url = workflow + "/workflow/startMeetingProcess";
        //请求工作流接口，开启工作流
        HttpResponse response = HttpRequest.post(url).header("Content-Type", "application/json").body(json.toString()).execute();
        if (response.getStatus() == 200) {
            json = JSONUtil.parseObj(response.body());
            //如果工作流创建成功，就更新会议状态
            String instanceId = json.getStr("instanceId");
            if (StringUtils.isBlank(instanceId)) {
                throw new GlobalException(RespCodeEnum.FAIL.getCode(), "生成会议工作流实例ID失败");
            }
            int row = meetingDao.updateMeetingInstanceId(uuid, instanceId); //在会议记录中保存工作流实例的ID
            if (row != 1) {
                throw new GlobalException(RespCodeEnum.FAIL.getCode(), "保存会议工作流实例ID失败");
            }
        }
    }
}
