package com.vacomall.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.vacomall.common.util.CommonUtil;
import com.vacomall.entity.*;
import com.vacomall.mapper.WorkFlowInstanceMapper;
import com.vacomall.service.*;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class WorkFlowInstanceServiceImpl extends ServiceImpl<WorkFlowInstanceMapper, WorkFlowInstance> implements WorkFlowInstanceService {

    @Autowired

    private WorkFlowInstanceMapper workFlowInstanceMapper;
    @Autowired
    private WorkFlowNodeService workFlowNodeService;
    @Autowired
    private ApplyService applyService;
    @Autowired
    private RomeService romeService;
    @Autowired
    private MeetingScheduleService meetingScheduleService;
    @Autowired
    private WorkFlowService workFlowService;
    @Autowired
    private ISysUserService userService;

    @Override
    public Page<WorkFlowInstance> getWorkFlowInstance(String userId, Date beginTime, Date endTime, String name, Page<WorkFlowInstance> page) {
        List<WorkFlowInstance> instanceList;
        Wrapper<WorkFlowInstance> wrapper = new EntityWrapper<>();

        if (!StringUtils.isEmpty(userId)) {
            wrapper.eq("applyUser", userId);
        }
        if (null != beginTime && null != endTime) {
            wrapper.between("createTime", beginTime, endTime);
        }
        if (null != beginTime && endTime == null) {
            wrapper.gt("createTime", beginTime);
        }
        if (null == beginTime && endTime != null) {
            wrapper.lt("createTime", endTime);
        }
        if (!StringUtils.isEmpty(name)) {
            wrapper.like("name", name);
        }
        Page<WorkFlowInstance> workFlowInstancePage = this.selectPage(page, wrapper);
        return workFlowInstancePage;
    }

    /**
     * 流程办结
     * @param applyId
     * @param instanceId
     * @param nodeId
     * @param isPass
     * @param opinion
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void concludeWorkFlow(String applyId, String instanceId, String nodeId,Integer isPass,String opinion) {
        WorkFlowInstance workFlowInstance = this.selectById(instanceId);
        workFlowInstance.setIsEnd(1);
        workFlowInstance.setIsPass(isPass);
        this.updateAllColumnById(workFlowInstance);
        WorkFlowNode workFlowNode = this.workFlowNodeService.selectById(nodeId);
        workFlowNode.setOpinion(opinion);
        workFlowNode.setEndTime(new Date());
        SysUser user = (SysUser) SecurityUtils.getSubject().getPrincipal();
        workFlowNode.setReviewerId(user.getId());
        workFlowNode.setReviewerName(user.getUserName());
        workFlowNode.setEndTime(new Date());
        //通过
        if(isPass.equals(1)){
            //设置通过状态
            workFlowNode.setStatus(2);
            //如果是通过则需要设置实验室使用时间
            Apply apply = applyService.selectById(applyId);
            MeetingRome rome = romeService.selectById(apply.getRomeId());
            MeetingSchedule meetingSchedule = new MeetingSchedule();
            meetingSchedule.setApplyid(applyId);
            meetingSchedule.setDetail(apply.getDetail());
            meetingSchedule.setRomeId(apply.getRomeId());
            meetingSchedule.setUse(apply.getName());
            meetingSchedule.setBeginTime(apply.getBeginTime());
            meetingSchedule.setEndTime(apply.getEndTime());
            meetingScheduleService.insert(meetingSchedule);
        }else{//不通过
            workFlowNode.setStatus(1);
        }
        workFlowNodeService.updateAllColumnById(workFlowNode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void turnTask(String applyId, String instanceId, String nodeId, String opinion,String nextUserId) {
        //获取流程下一节点
       WorkFlowInstance workFlowInstance = this.selectById(instanceId);
       WorkFlow workFlow = workFlowService.selectById(workFlowInstance.getWorkFlowId());
       WorkFlowNode workFlowNode = workFlowNodeService.selectById(nodeId);
       //办结本节点
        workFlowNode.setOpinion(opinion);
        workFlowNode.setEndTime(new Date());
        SysUser user = (SysUser) SecurityUtils.getSubject().getPrincipal();
        workFlowNode.setReviewerId(user.getId());
        workFlowNode.setReviewerName(user.getUserName());
        workFlowNode.setEndTime(new Date());
        //设置通过状态
        workFlowNode.setStatus(2);
        workFlowNodeService.updateAllColumnById(workFlowNode);

       //下一节点信息
        Map<String, String> nodeMap = CommonUtil.getWorkFlowNodeMap(workFlow,workFlowNode.getNodeIndex() + 1);
        //再次判断是否有下一节点
        boolean nextIsEmpty = CollectionUtils.isEmpty(nodeMap);
        //如果不存在下一节点则办结
        if(nextIsEmpty){
            this.concludeWorkFlow(applyId,instanceId,nodeId,1,opinion);
        }else{
            //否则转发到下一节点
            WorkFlowNode nextNode = new WorkFlowNode();
            //转发给指定的用户
            SysUser sysUser = userService.selectById(nextUserId);
            nextNode.setStatus(0);
            nextNode.setBeginTime(new Date());
            nextNode.setReviewerId(sysUser.getId());
            nextNode.setReviewerName(sysUser.getUserName());
            nextNode.setWorkFlowInstanceId(workFlowInstance.getId());
            nextNode.setNodeIndex(workFlowNode.getNodeIndex()+1);
            workFlowNodeService.insert(nextNode);
            //更新流程所在节点
            workFlowInstance.setNodeIndex(nextNode.getNodeIndex());
            this.updateAllColumnById(workFlowInstance);
        }

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void turnBack(String applyId, String instanceId, String nodeId, String opinion) {
        //获取当前节点
        WorkFlowNode workFlowNode = workFlowNodeService.selectById(nodeId);
        SysUser user = (SysUser) SecurityUtils.getSubject().getPrincipal();
        workFlowNode.setEndTime(new Date());
        workFlowNode.setReviewerName(user.getUserName());
        workFlowNode.setReviewerId(user.getId());
        workFlowNode.setOpinion(opinion);
        int currentIndex = workFlowNode.getNodeIndex();
        //设置退回状态
        workFlowNode.setStatus(-1);
        workFlowNode.setNodeIndex(-1);
        //更新
        workFlowNodeService.updateAllColumnById(workFlowNode);
        //获取流程实例，更新当前流程所在的节点
        WorkFlowInstance workFlowInstance = this.selectById(instanceId);
        //获取上一节点
        int i = currentIndex - 1;
        //如果i小于0则说明退回的是申请人，否则退回到上一个节点
        if(i < 0){
            //不需要获取上一节点
            workFlowInstance.setNodeIndex(-1);//-1表示在申请人节点
        }else{
            //设置流程所在的位置
            workFlowInstance.setNodeIndex(i);
            //设置上一节点激活
            WorkFlowNode node = workFlowNodeService.getNodeByWorkFlowInstanceAndIndex(instanceId, i);
            workFlowInstance.setIsPass(-1);
            node.setStatus(0);
            workFlowNodeService.updateAllColumnById(node);
        }
        //更改
        this.updateAllColumnById(workFlowInstance);
    }

    @Override
    public List<Map<String, Object>> getDetail(String workFlowInstanceId) {
        if(StringUtils.isEmpty(workFlowInstanceId)){
            Wrapper<WorkFlowNode> wrapper = new EntityWrapper<>();
            wrapper.eq("workFlowInstanceId",workFlowInstanceId);
            wrapper.orderBy("endTime",true);
            List<Map<String, Object>> maps = workFlowNodeService.selectMaps(wrapper);
            return maps;
        }
        return null;
    }
}
