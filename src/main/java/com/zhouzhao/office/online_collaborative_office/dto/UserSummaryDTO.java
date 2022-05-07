package com.zhouzhao.office.online_collaborative_office.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@ApiModel("用户信息摘要DTO")
public class UserSummaryDTO extends BaseDTO {
    @ApiModelProperty("用户姓名")
    private String name;

    @ApiModelProperty("部门姓名")
    private String deptName;

    @ApiModelProperty("头像url")
    private String photo;

    @ApiModelProperty("用户状态 1在职  -1 离职")
    private Integer status;
}
