package com.vacomall.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * 会议室时间表实例
 *
 */
@TableName("t_schedule")
public class MeetingSchedule implements Serializable {
    private static final long serialVersionUID = -209673575705481131L;

    @TableId(type = IdType.UUID)
    private String id;//主键

    private String romeId;//会议室主键

    private String applyid;//申请流程id

    private String use;//会议名称

    private String detail;//会议详情

    private Date beginTime;//会议开始时间

    private Date endTime;//会议结束时间

    private Integer backout = 2;//是否撤销，1表示已撤销，其他的表示正常

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRomeId() {
        return romeId;
    }

    public void setRomeId(String romeId) {
        this.romeId = romeId;
    }

    public String getApplyid() {
        return applyid;
    }

    public void setApplyid(String applyid) {
        this.applyid = applyid;
    }

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getBackout() {
        return backout;
    }

    public void setBackout(Integer backout) {
        this.backout = backout;
    }
}
