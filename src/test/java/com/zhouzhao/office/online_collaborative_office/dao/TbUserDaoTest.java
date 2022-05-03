package com.zhouzhao.office.online_collaborative_office.dao;


import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhouzhao.office.online_collaborative_office.entity.TbCheckin;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
class TbUserDaoTest {

    @Autowired
    TbCheckinDao checkinDao;


    @Test
    public void testUserDao() {
        QueryWrapper<TbCheckin> eq = new QueryWrapper<TbCheckin>().eq("user_id", "1512112152905854978").eq("date", DateUtil.today());
        List<TbCheckin> weekCheckin = checkinDao.getWeekCheckin(",", ",", "");
        for (TbCheckin tbCheckin : weekCheckin) {
            //System.out.println("tbCheckin = " +);
        }
    }

}