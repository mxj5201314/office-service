package com.zhouzhao.office.online_collaborative_office.vo;

import cn.hutool.core.date.DateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel("校验是否可以签到VO")
@Accessors(chain = true)
public class ValidCheckinVO {
    @NotBlank(message = "用户id不能为空")
    @ApiModelProperty(value = "用户id ", required = true)
    private String userId;

    @NotBlank(message = "日期不能为空")
    @ApiModelProperty(value = "日期 ", required = true)
    private String date;

    private DateTime startTime;
    private DateTime endTime;


}
