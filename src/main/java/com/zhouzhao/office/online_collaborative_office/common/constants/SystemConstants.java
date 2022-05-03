package com.zhouzhao.office.online_collaborative_office.common.constants;


import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class SystemConstants {
    //上班考勤开始时间
    public String attendanceStartTime;

    //上班时间
    public String attendanceTime;

    //上班考情截止时间
    public String attendanceEndTime;

    //下班考勤开始时间
    public String closingStartTime;

    //下班时间
    public String closingTime;

    //下班考勤截止时间
    public String closingEndTime;

}
