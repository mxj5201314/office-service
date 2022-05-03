package com.zhouzhao.office.online_collaborative_office.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel("登录信息VO")
public class LoginVO {
    @NotBlank(message = "微信授权Code不能为空")
    @ApiModelProperty(value = "微信登录提供的临时code ", required = true)
    private String code;

    private Integer code1;
}
