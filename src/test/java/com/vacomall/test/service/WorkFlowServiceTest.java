package com.vacomall.test.service;

import com.alibaba.fastjson.JSONArray;
import com.vacomall.entity.WorkFlow;
import com.vacomall.service.WorkFlowService;
import com.vacomall.test.SpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class WorkFlowServiceTest extends SpringTest {
    @Autowired
    private WorkFlowService workFlowService;

    /**
     * 流程初始化
     */
    @Test
    public void init(){
        WorkFlow workFlow = new WorkFlow();
        workFlow.setType("学生活动");
        List<Map> nodes = new ArrayList<>();
        Map<String,String> node = new HashMap<>();
        node.put("nodeId", UUID.randomUUID().toString());
        node.put("roleId","2f74d96770cc46019f4b84f1e2f5c080");
        node.put("name","会议室审批人员审批");
        nodes.add(node);
        Map<String,String> node2 = new HashMap<>();
        node2.put("nodeId", UUID.randomUUID().toString());
        node2.put("roleId","103ec01757fb4d4d98f6c655b3dbb305");
        node2.put("name","会议室管理员审批");
        nodes.add(node2);
        workFlow.setWorkFlowDetail(JSONArray.toJSONString(nodes));
        workFlowService.insert(workFlow);
        WorkFlow workFlow2 = new WorkFlow();
        workFlow2.setType("行政会议");
        List<Map> nodes2 = new ArrayList<>();
        Map<String,String> node3 = new HashMap<>();
        node3.put("nodeId", UUID.randomUUID().toString());
        node3.put("roleId","2f74d96770cc46019f4b84f1e2f5c080");
        node3.put("name","会议室审批人员审批");
        nodes2.add(node3);
        Map<String,String> node4 = new HashMap<>();
        node4.put("nodeId", UUID.randomUUID().toString());
        node4.put("roleId","103ec01757fb4d4d98f6c655b3dbb305");
        node4.put("name","会议室管理员审批");
        nodes2.add(node4);
        Map<String,String> node5 = new HashMap<>();
        node5.put("nodeId", UUID.randomUUID().toString());
        node5.put("roleId","5aa4abaaa1f64ffb93fc24a59130f77d");
        node5.put("name","院长审批");
        nodes2.add(node5);
        workFlow2.setWorkFlowDetail(JSONArray.toJSONString(nodes2));
        workFlowService.insert(workFlow2);
    }
}
