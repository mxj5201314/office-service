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
 * 节假日表(TbHolidays)表实体类
 *
 * @author makejava
 * @since 2022-04-18 00:10:40
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_holidays")
public class TbHolidays extends Model<TbHolidays> {
    //主键
    @TableId
    private Integer id;
    //日期
    private Date date;

    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    public  Serializable pkVal() {
        return this.id;
    }
}

