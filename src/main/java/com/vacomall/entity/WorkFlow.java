package com.vacomall.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;

/**
 * 流程信息实体类，用于做
 */
@TableName("t_workflow")
public class WorkFlow implements Serializable {
    private static final long serialVersionUID = 733243737949309528L;

    private String type;//流程类型，科研、学生活动等
    @TableId(type = IdType.UUID)
    private String id;
    private String workFlowDetail;//流程节点信息

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWorkFlowDetail() {
        return workFlowDetail;
    }

    public void setWorkFlowDetail(String workFlowDetail) {
        this.workFlowDetail = workFlowDetail;
    }
}
