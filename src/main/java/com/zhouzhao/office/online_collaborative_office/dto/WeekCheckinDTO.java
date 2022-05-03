package com.zhouzhao.office.online_collaborative_office.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@ApiModel("周考勤记录DTO")
public class WeekCheckinDTO extends BaseDTO {
    @ApiModelProperty("日期")
    private String date;

    @ApiModelProperty("签到状态 当天考情时间未结束，但是还没有考勤 0     正常 1  迟到 2  缺勤3  放假4  未来（也就是没签到，因为没到时间呢）  -1")
    private Integer status;

    @ApiModelProperty("节假日或者工作日")
    private String type;

    @ApiModelProperty("星期几")
    private String day;
}
