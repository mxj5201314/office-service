package com.zhouzhao.office.online_collaborative_office.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel("我的考情记录DTO")
public class MyCheckinDTO {

    @ApiModelProperty("正常签到记录数")
    private Integer normalAttendance;

    @ApiModelProperty("迟到记录数")
    private Integer lateAttendance;

    @ApiModelProperty("缺勤记录数")
    private Integer absenteeism;

    @ApiModelProperty("签到记录")
    private List<WeekCheckinDTO> checkinList;
}
