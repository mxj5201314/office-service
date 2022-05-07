package com.zhouzhao.office.online_collaborative_office.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel("刷新信息DTO")
public class RefreshMessageDTO {

    @ApiModelProperty("接收了多少条消息")
    long lastRows;

    @ApiModelProperty("查询未读数据")
    long unreadRows;
}
