package com.zhouzhao.office.online_collaborative_office.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("更新会议信息VO")
public class UpdateMeetingVO {
    private Integer id;
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
    private String members;
    //会议内容
    private String desc;
    //工作流实例ID
    private String instanceId;
    private Integer status;

}
