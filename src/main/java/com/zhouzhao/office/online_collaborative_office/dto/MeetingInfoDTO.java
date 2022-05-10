package com.zhouzhao.office.online_collaborative_office.dto;


import com.zhouzhao.office.online_collaborative_office.entity.TbUser;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel("登录以后的信息DTO")
public class MeetingInfoDTO {

    //UUID
    private String uuid;
    //会议题目
    private String title;
    //创建人ID
    private Integer creatorId;
    //日期
    private String date;
    //开会地点
    private String place;
    //开始时间
    private String start;
    //结束时间
    private String end;
    //会议类型（1在线会议，2线下会议）
    private Integer type;
    //参与者
    private List<TbUser> members;
    //会议内容
    private String desc;
    //工作流实例ID
    private String instanceId;
    //状态（1未开始，2进行中，3已结束）
    private Integer status;

}
