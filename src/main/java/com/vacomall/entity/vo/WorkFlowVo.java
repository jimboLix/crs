package com.vacomall.entity.vo;

import com.vacomall.entity.WorkFlow;

import java.util.List;

/**
 * 流程扩展类，用于接收参数
 */
public class WorkFlowVo extends WorkFlow {
    private List<Node> nodeList;

    public List<Node> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<Node> nodeList) {
        this.nodeList = nodeList;
    }
}
