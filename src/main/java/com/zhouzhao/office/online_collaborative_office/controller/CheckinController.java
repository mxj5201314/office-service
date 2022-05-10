package com.zhouzhao.office.online_collaborative_office.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.zhouzhao.office.online_collaborative_office.common.Exception.GlobalException;
import com.zhouzhao.office.online_collaborative_office.common.config.shiro.JwtUtil;
import com.zhouzhao.office.online_collaborative_office.common.constants.CheckinConstants;
import com.zhouzhao.office.online_collaborative_office.common.constants.JwtConstant;
import com.zhouzhao.office.online_collaborative_office.common.constants.SystemConstants;
import com.zhouzhao.office.online_collaborative_office.common.enums.RespCodeEnum;
import com.zhouzhao.office.online_collaborative_office.common.utils.BaseResponse;
import com.zhouzhao.office.online_collaborative_office.dto.*;
import com.zhouzhao.office.online_collaborative_office.entity.TbUser;
import com.zhouzhao.office.online_collaborative_office.service.CheckinService;
import com.zhouzhao.office.online_collaborative_office.service.UserService;
import com.zhouzhao.office.online_collaborative_office.vo.CheckinVO;
import com.zhouzhao.office.online_collaborative_office.vo.MonthCheckinVO;
import com.zhouzhao.office.online_collaborative_office.vo.ValidCheckinVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import javax.validation.Valid;
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
    public BaseResponse validCanCheckIn(@RequestHeader(JwtConstant.HTTP_HEADER_NAME) String token) throws GlobalException {
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
    public BaseResponse getTodayCheckin(@RequestHeader(JwtConstant.HTTP_HEADER_NAME) String token) throws GlobalException {
        //int userId = jwtUtil.getUserId(token);
        Integer userId = jwtUtil.getUserId(token.substring(JwtConstant.TOKEN_HEAD.length()));
        TbUser user = userService.getUserById(userId);
        List<CheckInDTO> todayCheckin = checkinService.getTodayCheckin(token);
        Integer total = checkinService.getCheckinTotalByUserId(token);

        //判断日期是否在用户入职之前
        DateTime hiredate = DateUtil.parse(userService.getUserHiredate(userId));
        //获取当前日期周一的时间
        DateTime startDate = DateUtil.beginOfWeek(DateUtil.date());
        if (startDate.isBefore(hiredate)) {
            //如果周一的日期在他入职之前则开始时间为入职时间
            startDate = hiredate;
        }
        //这个日期的那一周的周日
        DateTime endDate = DateUtil.endOfWeek(DateUtil.date());
        List<WeekCheckinDTO> weekCheckinList = checkinService.getWeekCheckin(userId, startDate.toString(), endDate.toString());
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


    @PostMapping("/getMonthCheckin")
    @ApiOperation("查询用户某月签到数据")
    public BaseResponse searchMonthCheckin(@Valid @RequestBody MonthCheckinVO vo,
                                           @RequestHeader(JwtConstant.HTTP_HEADER_NAME) String token) throws GlobalException {
        Integer userId = jwtUtil.getUserId(token.substring(JwtConstant.TOKEN_HEAD.length()));
        //查询入职日期
        DateTime hiredate = DateUtil.parse(userService.getUserHiredate(userId));
        //把月份处理成双数字
        String month = vo.getMonth() < 10 ? "0" + vo.getMonth() : "" +
                vo.getMonth();
        //某年某月的起始日期
        DateTime startDate = DateUtil.parse(vo.getYear() + "-" + month + "-01");
        //如果查询的月份早于员工入职日期的月份就抛出异常
        if (startDate.isBefore(DateUtil.beginOfMonth(hiredate))) {
            throw new GlobalException(RespCodeEnum.FAIL.getCode(), "只能查询考勤之后日期的数据");
        }
        //如果查询月份与入职月份恰好是同月，本月考勤查询开始日期设置成入职日期
        if (startDate.isBefore(hiredate)) {
            startDate = hiredate;
        }
        //某年某月的截止日期
        DateTime endDate = DateUtil.endOfMonth(startDate);
        List<WeekCheckinDTO> weekCheckinList = checkinService.getWeekCheckin(userId, startDate.toString(), endDate.toString());
        //统计月考勤数据
        int sum_1 = 0, sum_2 = 0, sum_3 = 0;
        for (WeekCheckinDTO weekCheckinDTO : weekCheckinList) {
            String type = weekCheckinDTO.getType();
            Integer status = weekCheckinDTO.getStatus();
            if (CheckinConstants.TYPE_WORKING.equals(type)) {
                if (status == 1) {
                    //正常签到
                    sum_1++;
                } else if (status == 2) {
                    //迟到
                    sum_2++;
                } else if (status == 3) {
                    //缺勤
                    sum_3++;
                }
            }
        }
        MyCheckinDTO myCheckinDTO = new MyCheckinDTO()
                .setCheckinList(weekCheckinList)
                .setAbsenteeism(sum_3)
                .setLateAttendance(sum_2)
                .setNormalAttendance(sum_1);
        return BaseResponse.success(myCheckinDTO);
    }

}

