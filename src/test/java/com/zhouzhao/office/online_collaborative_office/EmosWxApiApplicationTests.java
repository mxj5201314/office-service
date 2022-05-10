package com.zhouzhao.office.online_collaborative_office;

import cn.hutool.core.util.IdUtil;
import com.zhouzhao.office.online_collaborative_office.common.Exception.GlobalException;
import com.zhouzhao.office.online_collaborative_office.entity.MessageEntity;
import com.zhouzhao.office.online_collaborative_office.entity.MessageRefEntity;
import com.zhouzhao.office.online_collaborative_office.service.MeetingService;
import com.zhouzhao.office.online_collaborative_office.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.util.Date;

@SpringBootTest
class EmosWxApiApplicationTests {

    @Autowired
    private MessageService messageService;

    @Autowired
    private MeetingService meetingService;


    @Test
    void contextLoads() {
        for (int i = 1; i <= 100; i++) {
            MessageEntity message = new MessageEntity();
            message.setUuid(IdUtil.simpleUUID());
            message.setSenderId(0);
            message.setSenderName("系统消息");
            message.setMsg("这是第" + i + "条测试消息");
            message.setSendTime(new Date());
            String id = messageService.insertMessage(message);
            MessageRefEntity ref = new MessageRefEntity();
            ref.setMessageId(id);
            ref.setReceiverId(627535873); //注意：这是接收人ID
            ref.setLastFlag(true);
            ref.setReadFlag(false);
            messageService.insertRef(ref);
        }
    }


    @Test
    void createMeetingData() throws GlobalException, ParseException {
        //for (int i = 1; i <= 100; i++) {
        //    TbMeeting meeting = new TbMeeting();
        //    meeting.setId(String.valueOf(i));
        //    meeting.setUuid(IdUtil.simpleUUID());
        //    meeting.setTitle("测试会议" + i);
        //    meeting.setCreatorId(15L); //ROOT用户ID
        //    meeting.setDate();
        //    meeting.setPlace("线上会议室");
        //    meeting.setStart("08:30");
        //    meeting.setEnd("10:30");
        //    meeting.setType(1);
        //    meeting.setMembers("[1512112152905854978]");
        //    meeting.setDesc("会议研讨Emos项目上线测试");
        //    meeting.setInstanceId(IdUtil.simpleUUID());
        //    meeting.setStatus(3);
        //    meeting.setCreateTime(new Date());
        //    meetingService.insertMeeting(meeting);
        //}
    }



}
