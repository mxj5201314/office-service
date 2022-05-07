package com.zhouzhao.office.online_collaborative_office.controller;


import com.zhouzhao.office.online_collaborative_office.common.config.shiro.JwtUtil;
import com.zhouzhao.office.online_collaborative_office.common.constants.JwtConstant;
import com.zhouzhao.office.online_collaborative_office.common.utils.BaseResponse;
import com.zhouzhao.office.online_collaborative_office.entity.TbMeeting;
import com.zhouzhao.office.online_collaborative_office.entity.getMyMeetingListByPageVO;
import com.zhouzhao.office.online_collaborative_office.service.MeetingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;

@RestController
@RequestMapping("/meeting")
@Api("会议模块网络接口")
public class MeetingController {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MeetingService meetingService;

    @PostMapping("/getMyMeetingListByPage")
    @ApiOperation("查询会议列表分页数据")
    public BaseResponse getMyMeetingListByPage(@Valid @RequestBody getMyMeetingListByPageVO vo, @RequestHeader(JwtConstant.HTTP_HEADER_NAME) String token) {
        String userId = jwtUtil.getUserId(token.substring(JwtConstant.TOKEN_HEAD.length()));
        int page = vo.getPage();
        int length = vo.getLength();
        int start = (page - 1) * length;

        ArrayList<TbMeeting> list = meetingService.getMyMeetingListByPage(userId, start, length);
        return BaseResponse.success(list);
    }
}
