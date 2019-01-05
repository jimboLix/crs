package com.vacomall.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/***
 * 时间表
 */
@TableName("t_tm_table")
public class TimeTable extends Model<TimeTable> {


    private static final long serialVersionUID = 5917869897779230342L;

    @TableId(type= IdType.UUID)
    private String id;

    //使用时间
    private Date useTime;

    //会议室的主键，表示在useTime时间id为romeId被使用
    private String romeId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getUseTime() {
        return useTime;
    }

    public void setUseTime(Date useTime) {
        this.useTime = useTime;
    }

    public String getRomeId() {
        return romeId;
    }

    public void setRomeId(String romeId) {
        this.romeId = romeId;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
