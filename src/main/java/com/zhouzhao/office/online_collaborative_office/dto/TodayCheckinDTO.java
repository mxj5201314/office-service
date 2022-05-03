package com.zhouzhao.office.online_collaborative_office.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@ApiModel("考情记录")
public class TodayCheckinDTO extends BaseDTO {
    @ApiModelProperty("姓名")
    String name;
    @ApiModelProperty("头像")
    String photo;
    @ApiModelProperty("部门名称")
    String deptName;
    @ApiModelProperty("今天时间")
    String today;

    @ApiModelProperty("上班考勤开始时间")
    String attendanceTime;

    @ApiModelProperty("下班考勤结束时间")
    String closingTime;

    @ApiModelProperty("总签到天数")
    Integer total;

    @ApiModelProperty("今天的签到记录")
    List<CheckInDTO> todayCheckins;

    @ApiModelProperty("一周签到记录")
    List<WeekCheckinDTO> weekCheckinList;
}
