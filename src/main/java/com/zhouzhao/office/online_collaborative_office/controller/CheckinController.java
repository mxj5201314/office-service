package com.zhouzhao.office.online_collaborative_office.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.zhouzhao.office.online_collaborative_office.common.Exception.GlobalException;
import com.zhouzhao.office.online_collaborative_office.common.config.shiro.JwtUtil;
import com.zhouzhao.office.online_collaborative_office.common.constants.JwtConstant;
import com.zhouzhao.office.online_collaborative_office.common.constants.SystemConstants;
import com.zhouzhao.office.online_collaborative_office.common.enums.RespCodeEnum;
import com.zhouzhao.office.online_collaborative_office.common.utils.BaseResponse;
import com.zhouzhao.office.online_collaborative_office.dto.CheckInDTO;
import com.zhouzhao.office.online_collaborative_office.dto.TodayCheckinDTO;
import com.zhouzhao.office.online_collaborative_office.dto.ValidCheckinDTO;
import com.zhouzhao.office.online_collaborative_office.dto.WeekCheckinDTO;
import com.zhouzhao.office.online_collaborative_office.entity.TbUser;
import com.zhouzhao.office.online_collaborative_office.service.CheckinService;
import com.zhouzhao.office.online_collaborative_office.service.UserService;
import com.zhouzhao.office.online_collaborative_office.vo.CheckinVO;
import com.zhouzhao.office.online_collaborative_office.vo.ValidCheckinVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.util.List;

@RequestMapping("/checkin")
@RestController
@Api("签到模块Web接口")
@Slf4j
public class CheckinController {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CheckinService checkinService;

    @Autowired
    private SystemConstants systemConstants;
    @Autowired
    private UserService userService;

    @GetMapping("/validCanCheckIn")
    @ApiOperation("查看用户今天是否可以签到")
    public BaseResponse validCanCheckIn(@RequestHeader(JwtConstant.HTTP_HEADER_NAME) String token) {
        ValidCheckinDTO result = checkinService.ValidCheckin(new ValidCheckinVO()
                .setUserId(jwtUtil.getUserId(token.substring(JwtConstant.TOKEN_HEAD.length())))
                .setDate(DateUtil.today()));
        return BaseResponse.success(result);
    }

    @PostMapping("/checkin")
    @ApiOperation("签到")
    public BaseResponse checkin(CheckinVO checkinVO, @RequestParam("photo") MultipartFile file,
                                @RequestHeader(JwtConstant.HTTP_HEADER_NAME) String token) throws IOException, GlobalException {
        if (null == file) {
            return BaseResponse.fail(RespCodeEnum.ERR_CHECKIN_PHOTO);
        }

        BASE64Encoder base64Encoder = new BASE64Encoder();
        String base64EncoderImg = base64Encoder.encode(file.getBytes());
        checkinService.checkin(checkinVO, base64EncoderImg, token);
        return BaseResponse.success();
    }


    @PostMapping("createFace")
    @ApiOperation("创建人脸数据")
    public BaseResponse createFace(@RequestParam("photo") MultipartFile file,
                                   @RequestHeader(JwtConstant.HTTP_HEADER_NAME) String token) throws IOException, GlobalException {
        if (null == file) {
            return BaseResponse.fail(RespCodeEnum.ERR_CHECKIN_PHOTO);
        }
        BASE64Encoder base64Encoder = new BASE64Encoder();
        String base64EncoderImg = base64Encoder.encode(file.getBytes());
        checkinService.createFace(base64EncoderImg, token);
        return BaseResponse.success();
    }


    @GetMapping("/getTodayCheckin")
    @ApiOperation("查询用户当日签到数据")
    public BaseResponse getTodayCheckin(@RequestHeader(JwtConstant.HTTP_HEADER_NAME) String token) {
        //int userId = jwtUtil.getUserId(token);
        String userId = jwtUtil.getUserId(token.substring(JwtConstant.TOKEN_HEAD.length()));
        TbUser user = userService.getUserById(userId);
        List<CheckInDTO> todayCheckin = checkinService.getTodayCheckin(token);
        Integer total = checkinService.getCheckinTotalByUserId(token);

        //判断日期是否在用户入职之前
        DateTime hiredate = DateUtil.parse(userService.getUserHiredate(token));
        //获取当前日期周一的时间
        DateTime startDate = DateUtil.beginOfWeek(DateUtil.date());
        if (startDate.isBefore(hiredate)) {
            //如果周一的日期在他入职之前则开始时间为入职时间
            startDate = hiredate;
        }
        //这个日期的那一周的周日
        DateTime endDate = DateUtil.endOfWeek(DateUtil.date());
        List<WeekCheckinDTO> weekCheckinList = checkinService.getWeekCheckin(token, startDate.toString(), endDate.toString());
        TodayCheckinDTO todayCheckinDTO = new TodayCheckinDTO()
                .setWeekCheckinList(weekCheckinList)
                .setTotal(total)
                .setClosingTime(systemConstants.getClosingTime())
                .setAttendanceTime(systemConstants.getAttendanceEndTime())
                .setTodayCheckins(todayCheckin)
                .setToday(DateUtil.today())
                .setName(user.getName())
                .setPhoto(user.getPhoto())
                .setDeptName(user.getDeptName());

        return BaseResponse.success(todayCheckinDTO);
    }
}

