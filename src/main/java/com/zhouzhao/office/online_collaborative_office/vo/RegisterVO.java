package com.zhouzhao.office.online_collaborative_office.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@ApiModel("注册信息VO")
public class RegisterVO {

    @NotBlank(message = "注册码不能为空")
    @Pattern(regexp = "^[0-9]{6}$", message = "注册码必须是6位数字")
    @ApiModelProperty(value = "注册码 ", required = true)
    private String registerCode;

    @NotBlank(message = "微信授权Code不能为空")
    @ApiModelProperty(value = "微信登录提供的临时code ", required = true)
    private String code;

    @NotBlank(message = "昵称不能为空")
    @ApiModelProperty(value = "昵称 ", required = true)
    private String nickname;

    @NotBlank(message = "头像不能为空")
    @ApiModelProperty(value = "头像 ", required = true)
    private String photo;


}
