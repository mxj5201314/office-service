package com.zhouzhao.office.online_collaborative_office.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel("签到VO")
public class CheckinVO {
    @NotBlank(message = "地址不能为空")
    private String address;

    @NotBlank(message = "国家不能为空")
    private String nation;

    @NotBlank(message = "省份不能为空")
    private String province;

    @NotBlank(message = "城市不能为空")
    private String city;

    @NotBlank(message = "区不能为空")
    private String district;
}
