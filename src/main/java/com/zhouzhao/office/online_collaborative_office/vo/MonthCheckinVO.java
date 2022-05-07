package com.zhouzhao.office.online_collaborative_office.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class MonthCheckinVO {
    @NotNull(message = "年份不能为空")
    @Range(min = 2000, max = 3000)
    @ApiModelProperty("年份")
    private Integer year;

    @NotNull(message = "月份不能为空")
    @Range(min = 1, max = 12)
    @ApiModelProperty("月份")
    private Integer month;
}
