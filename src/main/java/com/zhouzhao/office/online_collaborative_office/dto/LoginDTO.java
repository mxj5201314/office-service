package com.zhouzhao.office.online_collaborative_office.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Set;

@Data
@Accessors(chain = true)
@ApiModel("登录以后的信息DTO")
public class LoginDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("权限列表")
    private Set<String> permissions;

    @ApiModelProperty("登录令牌")
    private String token;
}
