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
        //获取流程实例
        WorkFlowInstance workFlowInstance = this.selectById(instanceId);
        //设置流程结束状态
        workFlowInstance.setIsEnd(1);
        //设置是否通过（同意 or 不同意）
        workFlowInstance.setIsPass(isPass);
        //更新流程实例
        this.updateAllColumnById(workFlowInstance);
        //获取流程节点
        WorkFlowNode workFlowNode = this.workFlowNodeService.selectById(nodeId);
        //设置当前节点意见
        workFlowNode.setOpinion(opinion);
        //设置流程节点结束时间
        workFlowNode.setEndTime(new Date());
        //获取当前系统用户
        SysUser user = (SysUser) SecurityUtils.getSubject().getPrincipal();
        //设置当前节点审批人员
        workFlowNode.setReviewerId(user.getId());
        workFlowNode.setReviewerName(user.getUserName());
        //通过
        if(isPass.equals(1)){
            //设置通过状态
            workFlowNode.setStatus(2);
            //如果是通过则需要设置实验室使用时间
            Apply apply = applyService.selectById(applyId);
            //以下是设置会议室的调度表，即会议室时间安排
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean backOut(String instanceId) {
        //根据id获取实例流程
        WorkFlowInstance workFlowInstance = this.selectById(instanceId);
        //判断流程是否结束
        if(workFlowInstance != null){
            Integer isEnd = workFlowInstance.getIsEnd();
            //没有结束
            if(null != isEnd && isEnd != 1){
                workFlowInstance.setIsEnd(2);
                //设置流程当前所在节点的状态为已撤销
                //先获取当前所在节点
                Wrapper<WorkFlowNode> wrapper = new EntityWrapper<>();
                wrapper.eq("workFlowInstanceId",instanceId);
                wrapper.eq("nodeIndex",workFlowInstance.getNodeIndex());
                WorkFlowNode workFlowNode = workFlowNodeService.selectOne(wrapper);
                //设置该节点状态为已撤销
                workFlowNode.setStatus(3);
                workFlowNodeService.updateById(workFlowNode);
            }else if(null != isEnd && isEnd == 1){
                //结束的，也是设置状态是已撤销，且将会议室调度表的记录设为已撤销
                workFlowInstance.setIsEnd(2);
                //根据instanceId查apply
                Wrapper<Apply> wrapper = new EntityWrapper<>();
                wrapper.eq("workFlowInstanceId",instanceId);
                Apply apply = applyService.selectOne(wrapper);
                if(null != apply){
                    //根据apply Id获取t_schedule
                    Wrapper<MeetingSchedule> mWraper = new EntityWrapper<>();
                    mWraper.eq("applyid",apply.getId());
                    MeetingSchedule meetingSchedule = meetingScheduleService.selectOne(mWraper);
                    if(meetingSchedule != null){
                        meetingSchedule.setBackout(1);
                        meetingScheduleService.updateById(meetingSchedule);
                    }
                }
            }
        }
        this.updateById(workFlowInstance);
        return true;
    }
}
