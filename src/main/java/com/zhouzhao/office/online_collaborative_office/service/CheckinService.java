package com.zhouzhao.office.online_collaborative_office.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhouzhao.office.online_collaborative_office.common.Exception.GlobalException;
import com.zhouzhao.office.online_collaborative_office.dto.CheckInDTO;
import com.zhouzhao.office.online_collaborative_office.dto.ValidCheckinDTO;
import com.zhouzhao.office.online_collaborative_office.dto.WeekCheckinDTO;
import com.zhouzhao.office.online_collaborative_office.entity.TbUser;
import com.zhouzhao.office.online_collaborative_office.vo.CheckinVO;
import com.zhouzhao.office.online_collaborative_office.vo.ValidCheckinVO;

import java.util.List;

public interface CheckinService extends IService<TbUser> {
    ValidCheckinDTO ValidCheckin(ValidCheckinVO validCheckinVO);

    void checkin(CheckinVO checkinVO, String image, String token) throws GlobalException;

    void createFace(String image, String token) throws GlobalException;

    List<CheckInDTO> getTodayCheckin(String token) throws GlobalException;

    Integer getCheckinTotalByUserId(String token) throws GlobalException;

    List<WeekCheckinDTO> getWeekCheckin(Integer userId, String startDate, String endDate);

}
