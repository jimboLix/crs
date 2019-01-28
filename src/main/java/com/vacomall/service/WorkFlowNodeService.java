package com.vacomall.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.vacomall.entity.WorkFlowNode;
import com.vacomall.entity.vo.WorkFlowNodeVo;

import java.util.List;

public interface WorkFlowNodeService extends IService<WorkFlowNode> {

    /**
     * 获取我的任务
     * @param userId 用户id
     * @param status 任务状态 0.活动。1.通过。2。退回
     * @return
     */
    Page<WorkFlowNodeVo> myTasks(String userId, Integer status, Integer pageNo, Integer pageSize);

    /**
     * 根据流程实例id和流程所在节点位置获取流程节点信息
     * @param workFlowInstanceId
     * @param nodeIndex
     * @return
     */
    WorkFlowNode getNodeByWorkFlowInstanceAndIndex(String workFlowInstanceId,Integer nodeIndex);
}
