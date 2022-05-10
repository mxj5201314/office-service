package com.zhouzhao.office.online_collaborative_office.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


@ApiModel
@Data
public class MeetingByIdVO {
    @NotNull
    @Min(1)
    private Integer id;
}
