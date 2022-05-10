package com.zhouzhao.office.online_collaborative_office.controller;


import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.http.HtmlUtil;
import cn.hutool.json.JSONUtil;
import com.zhouzhao.office.online_collaborative_office.common.Exception.GlobalException;
import com.zhouzhao.office.online_collaborative_office.common.config.shiro.JwtUtil;
import com.zhouzhao.office.online_collaborative_office.common.config.tencent.TLSSigAPIv2;
import com.zhouzhao.office.online_collaborative_office.common.constants.JwtConstant;
import com.zhouzhao.office.online_collaborative_office.common.enums.RespCodeEnum;
import com.zhouzhao.office.online_collaborative_office.common.utils.BaseResponse;
import com.zhouzhao.office.online_collaborative_office.dto.MeetingInfoDTO;
import com.zhouzhao.office.online_collaborative_office.dto.UserSigDTO;
import com.zhouzhao.office.online_collaborative_office.entity.TbMeeting;
import com.zhouzhao.office.online_collaborative_office.service.MeetingService;
import com.zhouzhao.office.online_collaborative_office.service.UserService;
import com.zhouzhao.office.online_collaborative_office.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/meeting")
@Api("会议模块网络接口")
public class MeetingController {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private UserService userService;

    @Value("${trtc.appid}")
    private Integer appid;

    @Value("${trtc.key}")
    private String key;

    @Value("${trtc.expire}")
    private Integer expire;


    @PostMapping("/getMyMeetingListByPage")
    @ApiOperation("查询会议列表分页数据")
    public BaseResponse getMyMeetingListByPage(@Valid @RequestBody GetMyMeetingListByPageVO vo, @RequestHeader(JwtConstant.HTTP_HEADER_NAME) String token) throws GlobalException {
        Integer userId = jwtUtil.getUserId(token.substring(JwtConstant.TOKEN_HEAD.length()));
        int page = vo.getPage();
        int length = vo.getLength();
        int start = (page - 1) * length;

        List<TbMeeting> list = meetingService.getMyMeetingListByPage(userId, start, length);
        return BaseResponse.success(list);
    }

    @PostMapping("/insertMeeting")
    @ApiOperation("添加会议")
    @RequiresPermissions(value = {"ROOT", "MEETING:INSERT"}, logical = Logical.OR)
    public BaseResponse insertMeeting(@Valid @RequestBody InsertMeetingVO vo, @RequestHeader(JwtConstant.HTTP_HEADER_NAME) String token) throws GlobalException {
        if (vo.getType() == 2 && (vo.getPlace() == null || vo.getPlace().length() == 0)) {
            throw new GlobalException(RespCodeEnum.FAIL.getCode(), "线下会议地点不能为空");
        }
        DateTime d1 = DateUtil.parse(vo.getDate() + " " + vo.getStart() + ":00");
        DateTime d2 = DateUtil.parse(vo.getDate() + " " + vo.getEnd() + ":00");
        if (d2.isBeforeOrEquals(d1)) {
            throw new GlobalException(RespCodeEnum.FAIL.getCode(), "结束时间必须大于开始时间");
        }
        if (!JSONUtil.isTypeJSONArray(HtmlUtil.unescape(vo.getMembers()))) {
            throw new GlobalException(RespCodeEnum.FAIL.getCode(), "members不是JSON数组");
        }
        TbMeeting entity = new TbMeeting();
        entity.setUuid(UUID.randomUUID().toString(true));
        entity.setTitle(vo.getTitle());
        entity.setCreatorId(jwtUtil.getUserId(token.substring(JwtConstant.TOKEN_HEAD.length())));
        entity.setDate(vo.getDate());
        entity.setPlace(vo.getPlace());
        entity.setStart(vo.getStart() + ":00");
        entity.setEnd(vo.getEnd() + ":00");
        entity.setType(vo.getType());
        entity.setMembers(HtmlUtil.unescape(vo.getMembers()));
        entity.setDesc(vo.getDesc());
        entity.setStatus(1);

        meetingService.insertMeeting(entity);
        return BaseResponse.success();
    }

    @PostMapping("/getMeetingById")
    @ApiOperation("根据ID查询会议")
    @RequiresPermissions(value = {"ROOT", "MEETING:SELECT"}, logical = Logical.OR)
    public BaseResponse searchMeetingById(@Valid @RequestBody MeetingByIdVO vo) {
        MeetingInfoDTO meetingById = meetingService.getMeetingById(vo.getId());
        return BaseResponse.success(meetingById);
    }

    @PostMapping("/updateMeeting")
    @ApiOperation("更新会议")
    @RequiresPermissions(value = {"ROOT", "MEETING:UPDATE"}, logical = Logical.OR)
    public BaseResponse updateMeetingInfo(@Valid @RequestBody UpdateMeetingVO vo) throws GlobalException {
        if (vo.getType() == 2 && (vo.getPlace() == null || vo.getPlace().length() == 0)) {
            throw new GlobalException(RespCodeEnum.FAIL.getCode(), "线下会议地点不能为空");
        }
        DateTime d1 = DateUtil.parse(vo.getDate() + " " + vo.getStart() + ":00");
        DateTime d2 = DateUtil.parse(vo.getDate() + " " + vo.getEnd() + ":00");
        if (d2.isBeforeOrEquals(d1)) {
            throw new GlobalException(RespCodeEnum.FAIL.getCode(), "结束时间必须大于开始时间");
        }
        vo.setMembers(HtmlUtil.unescape(vo.getMembers()));
        if (!JSONUtil.isTypeJSONArray(vo.getMembers())) {
            throw new GlobalException(RespCodeEnum.FAIL.getCode(), "members不是JSON数组");
        }
        vo.setStart(vo.getStart() + ":00");
        vo.setEnd(vo.getEnd() + ":00");
        vo.setStatus(1);
        meetingService.updateMeetingInfo(vo);
        return BaseResponse.success();
    }

    @PostMapping("/deleteMeetingById")
    @ApiOperation("根据ID删除会议")
    @RequiresPermissions(value = {"ROOT", "MEETING:DELETE"}, logical = Logical.OR)
    public BaseResponse deleteMeetingById(@Valid @RequestBody MeetingByIdVO vo) throws GlobalException {
        meetingService.deleteMeetingById(vo.getId());
        return BaseResponse.success();
    }


    @GetMapping("/genUserSig")
    @ApiOperation("生成用户签名")
    public BaseResponse genUserSig(@RequestHeader(JwtConstant.HTTP_HEADER_NAME) String token) throws GlobalException {
        int id = jwtUtil.getUserId(token.substring(JwtConstant.TOKEN_HEAD.length()));
        String email = userService.getMemberEmail(id);
        if (StringUtils.isBlank(email)) {
            throw new GlobalException(RespCodeEnum.FAIL.getCode(), "请完善邮箱信息");
        }
        TLSSigAPIv2 api = new TLSSigAPIv2(appid, key);
        String userSig = api.genUserSig(email, expire);
        return BaseResponse.success(new UserSigDTO(userSig, email));
    }

    @PostMapping("/getRoomIdByUUID")
    @ApiOperation("查询会议房间RoomID")
    public BaseResponse getRoomIdByUUID(@Valid @RequestBody RoomIdByUUIDVO vo) {
        long roomId = meetingService.getRoomIdByUUID(vo.getUuid());
        return BaseResponse.success(roomId);
    }

    @PostMapping("/getUserMeetingInMonth")
    @ApiOperation("查询某月用户的会议日期列表")
    public BaseResponse searchUserMeetingInMonth(@Valid @RequestBody UserMeetingInMonthVO vo, @RequestHeader(JwtConstant.HTTP_HEADER_NAME) String token) throws GlobalException {
        int userId = jwtUtil.getUserId(token.substring(JwtConstant.TOKEN_HEAD.length()));
        HashMap param = new HashMap();
        param.put("userId", userId);
        param.put("express", vo.getYear()+"/"+vo.getMonth());
        List<String> list = meetingService.getUserMeetingInMonth(param);
        return BaseResponse.success(list);
    }
}
