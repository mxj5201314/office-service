package com.zhouzhao.office.online_collaborative_office.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel
public class DeleteMessageRefByIdVO {
    @NotBlank
    private String id;
}
