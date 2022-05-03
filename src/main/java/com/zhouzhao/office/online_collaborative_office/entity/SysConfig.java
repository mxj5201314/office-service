package com.zhouzhao.office.online_collaborative_office.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * (SysConfig)表实体类
 *
 * @author makejava
 * @since 2022-04-09 16:35:35
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_config")
public class SysConfig extends Model<SysConfig> {
    //主键
    private String id;
    //参数名
    private String paramKey;
    //参数值
    private String paramValue;
    //状态
    private Boolean status;
    //备注
    private String remark;


    @Override
    public Serializable pkVal() {
        return this.id;
    }
}

