package com.zhouzhao.office.online_collaborative_office.vo;


import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@ApiModel
public class GetMembersVO {
    @NotEmpty(message = "members不能为空")
    private List<Integer> members;
}
