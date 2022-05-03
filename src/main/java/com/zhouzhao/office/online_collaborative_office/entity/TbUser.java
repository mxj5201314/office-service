package com.zhouzhao.office.online_collaborative_office.entity;


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
 * 用户表(TbUser)表实体类
 *
 * @author makejava
 * @since 2022-03-21 18:10:05
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_user")
public class TbUser extends Model<TbUser> {
    //主键
    @TableId
    private String id;
    //长期授权字符串
    private String openId;
    //昵称
    private String nickname;
    //头像网址
    private String photo;
    //姓名
    private String name;
    //性别
    private String sex;
    //手机号码
    private String tel;
    //邮箱
    private String email;
    //入职日期
    private Date hiredate;
    //角色
    private String role;
    //是否是超级管理员
    private Boolean root;
    //部门编号
    private String deptId;

    @TableField(exist = false, select = false)
    //部门名称
    private String deptName;
    //状态
    private Integer status;
    //创建时间
    private Date createTime;

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

