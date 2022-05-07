package com.zhouzhao.office.online_collaborative_office.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel
@Data
public class UpdateUnreadMessageVO {

    @NotBlank
    private String id;
}
