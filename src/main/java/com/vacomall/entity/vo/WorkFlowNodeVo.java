package com.vacomall.entity.vo;

import com.vacomall.entity.WorkFlowNode;

/**
 * 流程节点扩展类
 */
public class WorkFlowNodeVo extends WorkFlowNode {

    private String workFlowName;//流程名称
    private String applyUserName;//流程申请人

    public String getWorkFlowName() {
        return workFlowName;
    }

    public void setWorkFlowName(String workFlowName) {
        this.workFlowName = workFlowName;
    }

    public String getApplyUser() {
        return applyUserName;
    }

    public void setApplyUser(String applyUserName) {
        this.applyUserName = applyUserName;
    }
}
