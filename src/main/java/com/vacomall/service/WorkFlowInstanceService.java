package com.vacomall.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.vacomall.entity.WorkFlowInstance;
import com.vacomall.entity.WorkFlowNode;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface WorkFlowInstanceService extends IService<WorkFlowInstance> {

    Page<WorkFlowInstance> getWorkFlowInstance(String userId, Date beginTime, Date endTime, String name, Page<WorkFlowInstance> page);

    /**
     * 流程办结
     * @param applyId
     * @param instanceId
     * @param nodeId
     */
    void concludeWorkFlow(String applyId,String instanceId,String nodeId,Integer isPass,String opinion);

    void turnTask(String applyId, String instanceId, String nodeId, String opinion, String nextUserId);

    /**
     * 退回
     * @param applyId
     * @param instanceId
     * @param nodeId
     * @param opinion
     */
    void turnBack(String applyId,String instanceId,String nodeId,String opinion);

    /**
     * 查看流程的详细信息
     * @param workFlowInstanceId
     * @return
     */
    List<Map<String,Object>> getDetail(String workFlowInstanceId);
}
