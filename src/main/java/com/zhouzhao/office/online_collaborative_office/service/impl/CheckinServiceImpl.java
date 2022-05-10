package com.zhouzhao.office.online_collaborative_office.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateRange;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhouzhao.office.online_collaborative_office.common.Exception.GlobalException;
import com.zhouzhao.office.online_collaborative_office.common.components.BaiDuFaceHandler;
import com.zhouzhao.office.online_collaborative_office.common.config.shiro.JwtUtil;
import com.zhouzhao.office.online_collaborative_office.common.constants.CheckinConstants;
import com.zhouzhao.office.online_collaborative_office.common.constants.JwtConstant;
import com.zhouzhao.office.online_collaborative_office.common.constants.SystemConstants;
import com.zhouzhao.office.online_collaborative_office.common.enums.RespCodeEnum;
import com.zhouzhao.office.online_collaborative_office.common.utils.EpidemicLevelUtil;
import com.zhouzhao.office.online_collaborative_office.dao.*;
import com.zhouzhao.office.online_collaborative_office.dto.CheckInDTO;
import com.zhouzhao.office.online_collaborative_office.dto.ValidCheckinDTO;
import com.zhouzhao.office.online_collaborative_office.dto.WeekCheckinDTO;
import com.zhouzhao.office.online_collaborative_office.entity.TbCheckin;
import com.zhouzhao.office.online_collaborative_office.entity.TbFaceModel;
import com.zhouzhao.office.online_collaborative_office.entity.TbUser;
import com.zhouzhao.office.online_collaborative_office.service.CheckinService;
import com.zhouzhao.office.online_collaborative_office.task.EmailTask;
import com.zhouzhao.office.online_collaborative_office.vo.CheckinVO;
import com.zhouzhao.office.online_collaborative_office.vo.ValidCheckinVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Scope("prototype")
@Slf4j
public class CheckinServiceImpl extends ServiceImpl<TbUserDao, TbUser> implements CheckinService {
    @Autowired
    private SystemConstants systemConstants;

    @Autowired
    private TbHolidaysDao holidaysDao;

    @Autowired
    private TbWorkdayDao workdayDao;

    @Autowired
    private TbCheckinDao checkinDao;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BaiDuFaceHandler baiDuFaceHandler;

    @Autowired
    private TbFaceModelDao tbFaceModelDao;

    @Value("${office.email.hr}")
    private String hrEmail;

    @Autowired
    private EmailTask emailTask;

    @Autowired
    private TbUserDao userDao;


    @Override
    public ValidCheckinDTO ValidCheckin(ValidCheckinVO validCheckinVO) {

        String dayType = CheckinConstants.TYPE_WORKING;
        String message;
        Integer code;
        if (DateUtil.date().isWeekend()) {
            dayType = CheckinConstants.TYPE_HOLIDAYS;
        }
        if (holidaysDao.searchTodayIsHolidays() != null) {
            dayType = CheckinConstants.TYPE_HOLIDAYS;

        } else if (workdayDao.searchTodayIsWorkday() != null) {
            dayType = CheckinConstants.TYPE_WORKING;
        }
        if (dayType.equals(CheckinConstants.TYPE_HOLIDAYS)) {
            message = CheckinConstants.MESSAGE_HOLIDAYS;
            code = CheckinConstants.NOT_ALLOWED_CHECKIN;
        } else {
            DateTime now = DateUtil.date();
            String start = DateUtil.today() + " " + systemConstants.attendanceStartTime;
            String end = DateUtil.today() + " " + systemConstants.attendanceEndTime;
            DateTime attendanceStart = DateUtil.parse(start);
            DateTime attendanceEnd = DateUtil.parse(end);
            if (now.isBefore(attendanceStart)) {
                message = CheckinConstants.MESSAGE_WORKING_BEFORE_START_TIME;
                code = CheckinConstants.NOT_ALLOWED_CHECKIN;
            } else if (now.isAfter(attendanceEnd)) {
                message = CheckinConstants.MESSAGE_WORKING_AFTER_END_TIME;
                code = CheckinConstants.NOT_ALLOWED_CHECKIN;
            } else {
                validCheckinVO.setStartTime(attendanceStart).setEndTime(attendanceEnd);
                boolean bool = checkinDao.haveCheckIn(validCheckinVO) != null;
                message = bool ? CheckinConstants.MESSAGE_WORKING_NO : CheckinConstants.MESSAGE_WORKING_YES;
                code = bool ? CheckinConstants.NOT_ALLOWED_CHECKIN : CheckinConstants.ALLOWED_CHECKIN;
            }
        }
        return new ValidCheckinDTO(code, dayType, message);

    }

    @Override
    @Transactional
    public void checkin(CheckinVO checkinVO, String image, String token) throws GlobalException {
        log.error(checkinVO.toString());
        Integer epidemicLevel;
        //解析token得到userid
        Integer userId = jwtUtil.getUserId(token.substring(JwtConstant.TOKEN_HEAD.length()));

        //查询是否注册过人脸
        QueryWrapper<TbFaceModel> user_id = new QueryWrapper<TbFaceModel>().eq("user_id", userId);
        Long count = tbFaceModelDao.selectCount(user_id);
        if (count > 0) {
            //如果数据库中存在，百度智能云里面不存在，报错

            Boolean isExist = baiDuFaceHandler.faceIsExist(userId, BaiDuFaceHandler.LIVENESS_CONTROL_LOW, BaiDuFaceHandler.IMAGE_TYPE_BASE64, image);
            if (!isExist) {
                throw new GlobalException(RespCodeEnum.ERR_CHECKIN_FACE_RECOGNITION);
            }
        } else {
            //人脸不存在
            throw new GlobalException(RespCodeEnum.ERR_CHECKIN_FACE_NOT_EXISTS);
        }

        //疫情等级查询
        try {
            epidemicLevel = EpidemicLevelUtil.getEpidemicLevel(checkinVO.getDistrict());
        } catch (Exception e) {
            throw new GlobalException(RespCodeEnum.ERR_CHECKIN_EPIDEMIC_LEVEL.getCode(), e.getMessage());
        }
        System.out.println("epidemicLevel = " + epidemicLevel);
        //如果处于高风险地区时发送邮件提醒
        if (EpidemicLevelUtil.HEIGHT.equals(epidemicLevel)) {
            sendEmail(userId, checkinVO.getAddress());
        }

        //判断当前时间为签到的一个状态
        Date d1 = DateUtil.date(); //当前时间
        Date d2 = DateUtil.parse(DateUtil.today() + " " + systemConstants.attendanceTime); //上班时间
        Date d3 = DateUtil.parse(DateUtil.today() + " " + systemConstants.attendanceEndTime); //签到结束时间
        int status;
        if (d1.compareTo(d2) <= 0) {
            status = 1; // 正常签到
        } else if (d1.compareTo(d2) > 0 && d1.compareTo(d3) < 0) {
            status = 2; //迟到
        } else {
            throw new GlobalException(RespCodeEnum.ERR_CHECKIN_TIMEOUT);
        }

        TbCheckin tbCheckin = new TbCheckin();
        BeanUtils.copyProperties(checkinVO, tbCheckin);
        tbCheckin.setCreateTime(new Date())
                .setUserId(userId)
                .setRisk(epidemicLevel)
                .setDate(new Date())
                .setCreateTime(d1)
                .setStatus(status)
                .setCheckMode(1);
        boolean insert = false;

        try {
            insert = tbCheckin.insert();
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (!insert) {
            throw new GlobalException(RespCodeEnum.ERR_CHECKIN);
        }
    }

    @Override
    public void createFace(String image, String token) throws GlobalException {
        Integer userId = jwtUtil.getUserId(token.substring(JwtConstant.TOKEN_HEAD.length()));

        //    人脸不存在，第一次使用，记为存档
        String faceToken = baiDuFaceHandler.faceRegister(userId, BaiDuFaceHandler.LIVENESS_CONTROL_LOW, BaiDuFaceHandler.IMAGE_TYPE_BASE64, image);
        if (StringUtils.isNotBlank(faceToken)) {
            //人脸记录写入数据库
            TbFaceModel tbFaceModel = new TbFaceModel().setUserId(userId).setFaceToken(faceToken);
            try {
                tbFaceModelDao.insert(tbFaceModel);
            } catch (Exception e) {
                throw new GlobalException(RespCodeEnum.ERR_CHECKIN_EXISTS);
            }

        } else {
            throw new GlobalException(RespCodeEnum.ERR_CHECKIN_FACE_REGISTER);
        }
    }

    @Override
    public List<CheckInDTO> getTodayCheckin(String token) throws GlobalException {
        Integer userId = jwtUtil.getUserId(token.substring(JwtConstant.TOKEN_HEAD.length()));
        QueryWrapper<TbCheckin> eq = new QueryWrapper<TbCheckin>()
                .eq("user_id", userId)
                .eq("date", DateUtil.today());
        List<TbCheckin> tbCheckins = checkinDao.selectList(eq);
        List<CheckInDTO> checkInDTOS = new ArrayList<>();
        tbCheckins.forEach(item -> {
            CheckInDTO checkInDTO = new CheckInDTO();
            BeanUtils.copyProperties(item, checkInDTO);
            checkInDTO.setCheckinTime(DateUtil.format(item.getCreateTime(),"hh:mm"));
            checkInDTOS.add(checkInDTO);
        });
        return checkInDTOS;
    }


    @Override
    public Integer getCheckinTotalByUserId(String token) throws GlobalException {
        Integer userId = jwtUtil.getUserId(token.substring(JwtConstant.TOKEN_HEAD.length()));
        return checkinDao.getCheckinTotalByUserId(userId);
    }

    @Override
    public List<WeekCheckinDTO> getWeekCheckin(Integer userId, String startDate, String endDate) {

        //获取指定时间范围内的用户签到记录
        List<TbCheckin> weekCheckinList = checkinDao.getWeekCheckin(userId, startDate, endDate);

        //获取指定范围内的节假日信息
        List<String> holidaysInRangeList = holidaysDao.getHolidaysInRange(startDate, endDate);

        //获取指定范围内的工作日信息
        List<String> workdayInRangeList = workdayDao.getWorkdayInRange(startDate, endDate);

        DateTime startDateTemp = DateUtil.parseDate(startDate);
        DateTime endDateTemp = DateUtil.parseDate(endDate);

        //创建日期范围生成器 就是某个范围内的所有日期
        DateRange range = DateUtil.range(startDateTemp, endDateTemp, DateField.DAY_OF_MONTH);

        List<WeekCheckinDTO> list = new ArrayList<>();

        //循环这个时间范围内的日期
        range.forEach(one -> {
            String date = one.toString("yyyy-MM-dd");


            //判断今天是不是假期或者工作日
            String type = CheckinConstants.TYPE_WORKING;
            if (one.isWeekend()) {
                type = CheckinConstants.TYPE_HOLIDAYS;
            }
            if (holidaysInRangeList != null && holidaysInRangeList.contains(date)) {
                type = CheckinConstants.TYPE_HOLIDAYS;
            } else if (workdayInRangeList != null && workdayInRangeList.contains(date)) {
                type = CheckinConstants.TYPE_WORKING;
            }

            //签到状态 当天考情时间未结束，但是还没有考勤 0     正常 1  迟到 2  缺勤3  放假4  未来（也就是没签到，因为没到时间呢）  -1
            Integer status = -1;
            //是工作日的前提下  并且排除日期是在今天之后的
            if (type.equals(CheckinConstants.TYPE_WORKING) && DateUtil.compare(one, DateUtil.date()) <= 0) {

                boolean flag = false;
                for (TbCheckin tbCheckin : weekCheckinList) {
                    //格式化时间
                    String format = DateUtil.format(tbCheckin.getDate(), "yyyy-MM-dd");

                    //如果签到记录表里面有这条记录
                    if (date.equals(format)) {
                        //获取签到状态
                        status = tbCheckin.getStatus();
                        flag = true;
                        break;
                    }
                }

                //下面这里是做特殊判断，判断当天有没有没签到，但是还没到考勤结束时间的
                DateTime endTime = DateUtil.parse(DateUtil.today() + " " + systemConstants.attendanceEndTime);
                String today = DateUtil.today();
                //判断今天在没有过考勤时间下的打卡
                if (date.equals(today) && DateUtil.date().isBefore(endTime) && !flag) {
                    status = 0;
                } else if (!flag) {
                    //缺勤
                    status = 3;
                }
            } else if (type.equals(CheckinConstants.TYPE_HOLIDAYS) && DateUtil.compare(one, DateUtil.date()) <= 0) {
                //节假日
                status = 4;
            }

            WeekCheckinDTO weekCheckinDTO = new WeekCheckinDTO().setDate(date).setStatus(status).setType(type).setDay(one.dayOfWeekEnum().toChinese("周"));

            list.add(weekCheckinDTO);
        });
        return list;
    }


    private void sendEmail(Integer userId, String address) {
        TbUser tbUser = userDao.getNameAndDeptAndTel(userId);
        String name = tbUser.getName();
        String deptName = tbUser.getDeptName();
        deptName = deptName != null ? deptName : "";
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(hrEmail);
        message.setSubject("员工" + name + "身处高风险疫情地区警告");
        message.setText(deptName + "员工" + name + "，" + DateUtil.format(new Date(), "yyyy年MM月dd日 hh点mm分时") + "处于" + address
                + "，属于新冠疫情高风险地区，请及时与该员工联系，核实情况！联系电话：" + tbUser.getTel());
        emailTask.sendAsync(message);
    }
}
