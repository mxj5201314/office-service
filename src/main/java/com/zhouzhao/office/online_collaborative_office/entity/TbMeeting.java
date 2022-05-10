package com.zhouzhao.office.online_collaborative_office.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * 会议表(TbMeeting)表实体类
 *
 * @author makejava
 * @since 2022-05-07 11:30:56
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_meeting")
public class TbMeeting extends Model<TbMeeting> {
    //主键
    @TableId(type = IdType.AUTO)
    private Integer id;
    //UUID
    private String uuid;
    //会议题目
    private String title;
    //创建人ID
    private Integer creatorId;
    //日期
    private String date;
    //开会地点
    private String place;
    //开始时间
    private String start;
    //结束时间
    private String end;
    //会议类型（1在线会议，2线下会议）
    private Integer type;
    //参与者
    private String members;
    //会议内容
    private String desc;
    //工作流实例ID
    private String instanceId;
    //状态（1未开始，2进行中，3已结束）
    private Integer status;
    //创建时间
    private Date createTime;
    @TableField(exist = false, select = false)
    private String name;

    @TableField(exist = false, select = false)
    private String photo;


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

