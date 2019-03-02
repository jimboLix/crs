package com.vacomall.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.vacomall.entity.SysRole;
import com.vacomall.entity.WorkFlow;
import com.vacomall.mapper.WorkFlowMapper;
import com.vacomall.service.ISysRoleService;
import com.vacomall.service.WorkFlowService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WorkFlowServiceImpl extends ServiceImpl<WorkFlowMapper, WorkFlow> implements WorkFlowService {
    @Autowired
    private ISysRoleService roleService;
    @Override
    public List<Map> workFlowDetail(String id) {
        List<Map> resultList = new ArrayList<>();
        WorkFlow workFlow = this.selectById(id);
        if(workFlow != null){
            String workFlowDetail = workFlow.getWorkFlowDetail();
            if(StringUtils.isNotEmpty(workFlowDetail)){
                //将json字符串转为map
                List<HashMap> nodeList = JSONArray.parseArray(workFlowDetail, HashMap.class);
                //在nodeList中每个map都含有roleId角色id和name节点名称
                //对nodeList进行遍历，并查出每个节点的角色名称
                if(!CollectionUtils.isEmpty(nodeList)){
                    for (HashMap nodeMap : nodeList){
                        Map<String,String> resultMap = new HashMap<>();
                        resultMap.put("nodeName", (String) nodeMap.get("name"));
                        if(!CollectionUtils.isEmpty(nodeMap)){
                            //获取角色id
                            String roleId = (String) nodeMap.get("roleId");
                            //根据角色id获取角色信息
                            SysRole sysRole = roleService.selectById(roleId);
                            if(null != sysRole) {
                                resultMap.put("roleName", sysRole.getRoleName());
                            }
                        }
                        resultList.add(resultMap);
                    }
                }

            }
        }
        return resultList;
    }
}
