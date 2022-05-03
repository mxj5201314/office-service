package com.zhouzhao.office.online_collaborative_office.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 签到表(TbCheckin)表实体类
 *
 * @author makejava
 * @since 2022-04-18 00:43:36
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_checkin")
public class TbCheckin extends Model<TbCheckin> {
    //主键
    @TableId
    private String id;
    //用户ID
    private String userId;
    //签到地址
    private String address;
    //国家
    private String country;
    //省份
    private String province;
    //城市
    private String city;
    //区划
    private String district;
    //考勤结果
    private Integer status;
    //风险等级
    private Integer risk;
    //签到日期
    private Date date;
    //签到时间
    private Date createTime;

    //上班还是下班  上班 1 下班 0
    private Integer checkMode;

    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    public Serializable pkVal() {
        return this.id;
    }
}

