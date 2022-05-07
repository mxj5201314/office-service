package com.zhouzhao.office.online_collaborative_office.vo;


import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel("通过id查找信息VO")
public class MessageByIdVO {
    @NotBlank(message = "id不能为空")
    private String id;
}
