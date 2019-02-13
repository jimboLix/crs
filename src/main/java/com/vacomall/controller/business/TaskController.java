package com.vacomall.controller.business;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.vacomall.common.bean.Rest;
import com.vacomall.common.controller.SuperController;
import com.vacomall.common.util.CommonUtil;
import com.vacomall.entity.*;
import com.vacomall.entity.vo.WorkFlowNodeVo;
import com.vacomall.service.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 流程审批相关的控制器
 */
@RequestMapping("/task")
@Controller
public class TaskController extends SuperController {

    @Autowired
    private WorkFlowNodeService workFlowNodeService;
    @Autowired
    private WorkFlowInstanceService workFlowInstanceService;
    @Autowired
    private ISysUserService userService;
    @Autowired
    private ApplyService applyService;
    @Autowired
    private WorkFlowService workFlowService;
    @Autowired
    private RomeService romeService;
    @RequestMapping("/mytask/{pageNo}")
    public String myTask(Model model, Integer status, @PathVariable(name = "pageNo") Integer pageNo, Integer pageSize){
        //获取当前用户信息
        Subject subject = SecurityUtils.getSubject();
        SysUser sysUser = (SysUser) subject.getPrincipal();
        if(null == pageNo){
            pageNo = 1;
        }
        if(null == pageSize){
            pageSize = 10;
        }
        if(sysUser != null){
            Page<WorkFlowNodeVo> page = this.workFlowNodeService.myTasks(sysUser.getId(), status, pageNo, pageSize);
            model.addAttribute("pageData",page);
        }
        return "system/task/list";
    }

    /**
     * 审批
     * @param taskId
     * @param model
     * @return
     */
    @RequestMapping("/detail")
    public String taskDetail(String taskId,Model model){
        //获取节点信息
        WorkFlowNode workFlowNode = workFlowNodeService.selectById(taskId);
        model.addAttribute("current",workFlowNode);
        //获取流程实例信息
        String workFlowInstanceId = workFlowNode.getWorkFlowInstanceId();
        WorkFlowInstance workFlowInstance = workFlowInstanceService.selectById(workFlowInstanceId);
        //获取申请人信息
        SysUser sysUser = userService.selectById(workFlowInstance.getApplyUser());
        model.addAttribute("user",sysUser);
        model.addAttribute("flow",workFlowInstance);
        //获取申请信息
        Wrapper<Apply> applyWrapper = new EntityWrapper<>();
        applyWrapper.eq("workFlowInstanceId",workFlowInstanceId);
        Apply apply = applyService.selectOne(applyWrapper);
        model.addAttribute("apply",apply);
        //获取申请的会议室
        MeetingRome rome = romeService.selectById(apply.getRomeId());
        model.addAttribute("rome",rome);
        //获取本节点之前审核信息
//        for (int i = 0;i < nodeIndex;i++){
//            Wrapper<WorkFlowNode> wrapper = new EntityWrapper<>();
//            wrapper.eq("workFlowInstanceId",workFlowInstanceId);
//            wrapper.eq("nodeIndex",i);
//            WorkFlowNode workFlowNodes = workFlowNodeService.selectOne(wrapper);
//            if(null != workFlowNodes) {
//                allFlowNodes.add(workFlowNodes);
//            }
//        }
        Wrapper<WorkFlowNode> wrapper = new EntityWrapper<>();
        wrapper.eq("workFlowInstanceId",workFlowInstanceId);
        wrapper.orderBy("beginTime",false);
        List<WorkFlowNode> allFlowNodes = workFlowNodeService.selectList(wrapper);
        model.addAttribute("allNodes",allFlowNodes);
        //获取下一节点人员
        String workFlowId = workFlowInstance.getWorkFlowId();
        WorkFlow workFlow = workFlowService.selectById(workFlowId);
        //确定当前流程共有几个节点
        int length = 0;
        if(null != workFlow) {
            String workFlowDetail = workFlow.getWorkFlowDetail();
            if (StringUtils.isNotEmpty(workFlowDetail)) {
                ArrayList nodeList = JSON.parseObject(workFlowDetail, ArrayList.class);
                if (!CollectionUtils.isEmpty(nodeList)) {
                    length = nodeList.size();
                }
            }
        }
        //如果是最后一个节点，则显示办结按钮，且不用获取下一节点人员信息
        if(workFlowNode.getNodeIndex().equals(length - 1)){
            model.addAttribute("showEnd",true);
        }else{
            //获取下一节点的可以转发人员的列表
            Map<String, String> nodeMap = CommonUtil.getWorkFlowNodeMap(workFlow, workFlowNode.getNodeIndex() + 1);
            String roleId = nodeMap.get("roleId");
            if (StringUtils.isNotEmpty(roleId)) {
                List<SysUser> users = this.userService.getUsersByRole(roleId);
                model.addAttribute("users",users);
            }
        }
        return "system/task/detail";
    }

    @RequestMapping("/deal")
    @ResponseBody
    public Rest deal(String nextUser,String opinion,Integer status,String applyId,String instanceId,String nodeId,Boolean isEnd){
        isEnd = isEnd == null ? false:true;
        WorkFlowNode workFlowNode = workFlowNodeService.selectById(nodeId);
//        SysUser user = (SysUser) SecurityUtils.getSubject().getPrincipal();
//
//        workFlowNode.setEndTime(new Date());
//        workFlowNode.setStatus(status);
//        workFlowNode.setOpinion(opinion);
//        workFlowNodeService.updateAllColumnById(workFlowNode);
        //同意
        if(status.equals(2)){
            //流程办结
            if(isEnd){
                workFlowInstanceService.concludeWorkFlow(applyId,instanceId,nodeId,1,opinion);
            }else{
                //不是办结则转到下一节点
                workFlowInstanceService.turnTask(applyId,instanceId,nodeId,opinion,nextUser);
            }
        }else if(status.equals(1)){//不同意
            //流程办结 //如果不同意则办结此流程
//            if(isEnd){
                workFlowInstanceService.concludeWorkFlow(applyId,instanceId,nodeId,0,opinion);
//            }else{
//
//            }
        }else{
            //退回
            workFlowInstanceService.turnBack(applyId,instanceId,nodeId,opinion);

        }
        return Rest.ok();
    }

    @RequestMapping("/flow")
    public String taskFlow(String taskId,Model model){
        //获取节点信息
        WorkFlowNode workFlowNode = workFlowNodeService.selectById(taskId);
        //获取流程实例信息
        String workFlowInstanceId = workFlowNode.getWorkFlowInstanceId();
//        WorkFlowInstance workFlowInstance = workFlowInstanceService.selectById(workFlowInstanceId);
        Wrapper<WorkFlowNode> wrapper = new EntityWrapper<>();
        wrapper.eq("workFlowInstanceId",workFlowInstanceId);
        wrapper.orderBy("beginTime",false);
        List<WorkFlowNode> allFlowNodes = workFlowNodeService.selectList(wrapper);
        model.addAttribute("allNodes",allFlowNodes);
        return "system/task/flow";
    }
}
