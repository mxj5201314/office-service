package com.zhouzhao.office.online_collaborative_office.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidCheckinDTO extends BaseDTO {
    private Integer code;
    private String type;
    private String message;
}
