package com.vacomall.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.vacomall.common.bean.Rest;
import com.vacomall.entity.Apply;
import com.vacomall.entity.SysUser;
import com.vacomall.entity.WorkFlowInstance;
import com.vacomall.entity.WorkFlowNode;
import com.vacomall.mapper.ApplyMapper;
import com.vacomall.service.ApplyService;
import com.vacomall.service.WorkFlowInstanceService;
import com.vacomall.service.WorkFlowNodeService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
public class ApplyServiceImpl extends ServiceImpl<ApplyMapper, Apply> implements ApplyService  {
    @Autowired
    private WorkFlowInstanceService workFlowInstanceService;
    @Autowired
    private WorkFlowNodeService workFlowNodeService;
    @Transactional(rollbackFor = Exception.class)
    public Rest createApply(Apply apply, String workFlowId, String daterange, String createTime,String node) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        if(!StringUtils.isNotEmpty(workFlowId)){
            return Rest.failure("未说明使用时间");
        }
        String[] range = daterange.split("--");
        WorkFlowInstance workFlowInstance = new WorkFlowInstance();
        try {
            //设置申请时间
            apply.setApplyTime(dateFormat.parse(createTime));
            workFlowInstance.setCreateTime(dateFormat.parse(createTime));
            Date begin = dateFormat.parse(range[0]);
            Date end = dateFormat.parse(range[1]);
            apply.setBeginTime(begin);
            apply.setEndTime(end);
        } catch (ParseException e) {
            return Rest.failure("使用时间格式错误");
        }
        //设置申请的用户
        SysUser user = (SysUser) SecurityUtils.getSubject().getPrincipal();
        apply.setUserId(user.getId());
        //设置申请人
        workFlowInstance.setApplyUser(user.getId());
        workFlowInstance.setWorkFlowId(workFlowId);
        UUID uuid = UUID.randomUUID();
        //设置当前流程所处的位置
        workFlowInstance.setNodeIndex(0);
        //设置活动状态
        workFlowInstance.setIsPass(1);
        workFlowInstance.setName(apply.getName());
        //设置id
        workFlowInstance.setId(uuid.toString());
        apply.setWorkFlowInstanceId(uuid.toString());
        //初始化节点
        WorkFlowNode workFlowNode = new WorkFlowNode();
        //设置本节点是当前流程的第一个节点
        workFlowNode.setNodeIndex(0);
        //设置节点活动状态
        workFlowNode.setStatus(0);
        //设置节点审批人
        workFlowNode.setReviewerId(node);
        workFlowNode.setBeginTime(new Date());
        //把节点关联到流程实例上
        workFlowNode.setWorkFlowInstanceId(uuid.toString());
        //保存到数据库
        workFlowInstanceService.insert(workFlowInstance);
        workFlowNodeService.insert(workFlowNode);
        this.insert(apply);
        return Rest.ok();
    }


}
