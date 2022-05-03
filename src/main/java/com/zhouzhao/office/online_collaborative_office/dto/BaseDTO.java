package com.zhouzhao.office.online_collaborative_office.dto;


import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel("基础DTO")
public class BaseDTO {
    private String errMsg;
}
