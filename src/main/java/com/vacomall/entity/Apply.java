package com.vacomall.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;
@TableName("t_apply")
public class Apply extends Model<Apply> {

    private static final long serialVersionUID = 7431377568879159808L;
    @TableId(type = IdType.UUID)
    private String id;//申请主键
    private String name;//流程名称
    private String detail;//申请详情
    private String romeId;//申请的会议室
    private Date beginTime;//申请使用开始时间
    private Date endTime;//申请使用的结束时间
    private Date applyTime;//申请时间
    private String userId;//申请人的主键
    private String workFlowInstanceId;//流程实例的主键

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getRomeId() {
        return romeId;
    }

    public void setRomeId(String romeId) {
        this.romeId = romeId;
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

    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getWorkFlowInstanceId() {
        return workFlowInstanceId;
    }

    public void setWorkFlowInstanceId(String workFlowInstanceId) {
        this.workFlowInstanceId = workFlowInstanceId;
    }

    @Override
    protected Serializable pkVal() {
        return serialVersionUID;
    }
}
