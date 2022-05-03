package com.zhouzhao.office.online_collaborative_office.dto;


import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@ApiModel("签到DTO")
public class CheckInDTO extends BaseDTO {
    private String address;
    private Integer status;
    private Integer risk;
    private String checkinTime;
    private Date date;
}
