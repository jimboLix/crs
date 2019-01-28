package com.vacomall.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.vacomall.entity.WorkFlowNode;
import com.vacomall.entity.vo.WorkFlowNodeVo;
import com.vacomall.mapper.WorkFlowNodeMapper;
import com.vacomall.service.WorkFlowNodeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WorkFlowNodeServiceImpl extends ServiceImpl<WorkFlowNodeMapper, WorkFlowNode> implements WorkFlowNodeService {
    @Autowired
    private WorkFlowNodeMapper workFlowNodeMapper;
    @Override
    public Page<WorkFlowNodeVo> myTasks(String userId, Integer status,Integer pageNo,Integer pageSize) {
        List<WorkFlowNodeVo> workFlowNodes;
        Map<String,Object> params = new HashMap<>();
        params.put("userId",userId);
        params.put("status",status);
        Page<WorkFlowNodeVo> page = new Page<>();
        page.setCurrent(pageNo);
        page.setSize(pageSize);
        workFlowNodes = workFlowNodeMapper.myTasksPage(params,page);
        page.setRecords(workFlowNodes);
        return page;
    }

    @Override
    public WorkFlowNode getNodeByWorkFlowInstanceAndIndex(String workFlowInstanceId, Integer nodeIndex) {
        if(StringUtils.isNotEmpty(workFlowInstanceId) && null != nodeIndex) {
            Wrapper<WorkFlowNode> wrapper = new EntityWrapper<>();
            wrapper.eq("workFlowInstanceId", workFlowInstanceId);
            wrapper.eq("nodeIndex", nodeIndex);
            WorkFlowNode workFlowNode = this.selectOne(wrapper);
            return workFlowNode;
        }
        return null;
    }
}
