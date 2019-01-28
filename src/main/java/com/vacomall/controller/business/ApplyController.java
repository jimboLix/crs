package com.vacomall.controller.business;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.vacomall.common.bean.Rest;
import com.vacomall.common.controller.SuperController;
import com.vacomall.common.util.CommonUtil;
import com.vacomall.entity.*;
import com.vacomall.service.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/apply")
public class ApplyController extends SuperController {

    @Autowired
    private RomeService romeService;
    @Autowired
    private WorkFlowInstanceService workFlowInstanceService;
    @Autowired
    private WorkFlowService workFlowService;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private ISysUserRoleService userRoleService;
    @Autowired
    private ApplyService applyService;
    @Autowired
    private WorkFlowNodeService workFlowNodeService;

    @RequestMapping("/add")
    public String romeApply(String romeId, Model model){
        //先获取申请的会议室信息
        MeetingRome rome = romeService.selectById(romeId);
        model.addAttribute("rome",rome);
        Subject subject = SecurityUtils.getSubject();
        SysUser user = (SysUser) subject.getPrincipal();
        model.addAttribute("sysUser",user);
        return "system/apply/add";
    }

    @RequestMapping("/list/{pageNo}")
    public String applyList(@PathVariable Integer pageNo, Date beginTime, Date endTime, String name, Integer pageSize,Model model){
        if(null == pageNo){
            pageNo = 1;
        }
        if(pageSize == null){
            pageSize = 10;
        }
        Subject subject = SecurityUtils.getSubject();
        SysUser user = (SysUser) subject.getPrincipal();
        String userId = user.getId();
        Page<WorkFlowInstance> page = getPage(pageNo, pageSize);
        Page<WorkFlowInstance> instances = this.workFlowInstanceService.getWorkFlowInstance(userId, beginTime, endTime, name, page);
        model.addAttribute("pageData",instances);
        return "system/apply/list";
    }

    /**
     * 创建申请
     * @return
     */
    @RequestMapping("/toCreate")
    public String toCreate(Model model){
        //获取所有的会议室以供选择
        List<MeetingRome> romeList = romeService.selectList(new EntityWrapper<>());
        model.addAttribute("romesList",romeList);
        //获取流程类型以供选择
        List<WorkFlow> workFlows = workFlowService.selectList(new EntityWrapper<>());
        model.addAttribute("workFlows",workFlows);
        //初始化一个申请表单
        Apply apply = new Apply();
        //当前用户信息
        SysUser user = (SysUser) SecurityUtils.getSubject().getPrincipal();
        apply.setUserId(user.getId());
        model.addAttribute("apply",apply);
        WorkFlowInstance workFlowInstance = new WorkFlowInstance();
        workFlowInstance.setApplyUser(apply.getUserId());
        workFlowInstance.setCreateTime(new Date());
        model.addAttribute("workIns",workFlowInstance);
        return "system/apply/create";
    }

    /**
     * 创建申请
     * @return
     */
    @RequestMapping("/doCreate")
    @ResponseBody
    public Rest doCreate(Apply apply,String workFlowId,String daterange,String createTime,String node)  {
        return applyService.createApply(apply, workFlowId, daterange, createTime,node);
    }

    @RequestMapping("/getNextNodeUser")
    @ResponseBody
    public Rest getNextNodeUser(String workFlowId){
        List<SysUser> userList = new ArrayList<>();
        WorkFlow workFlow = this.workFlowService.selectById(workFlowId);
        if(null != workFlow) {
            String workFlowDetail = workFlow.getWorkFlowDetail();
            if (StringUtils.isNotEmpty(workFlowDetail)) {
                ArrayList nodeList = JSON.parseObject(workFlowDetail, ArrayList.class);
                if (!CollectionUtils.isEmpty(nodeList)) {
                    Map<String, String> node = (Map<String, String>) nodeList.get(0);
                    String roleId = node.get("roleId");
                    if (StringUtils.isNotEmpty(roleId)) {
                        userList = this.sysUserService.getUsersByRole(roleId);
                    }
                }
            }
        }
       return Rest.okData(userList);
    }

    @RequestMapping("/edit")
    public String edit(Model model,String workFlowInstanceId){
        //先获取流程信息
        WorkFlowInstance workFlowInstance = workFlowInstanceService.selectById(workFlowInstanceId);
        model.addAttribute("workIns",workFlowInstance);
        Wrapper<WorkFlowNode> wrapper = new EntityWrapper<>();
        wrapper.eq("workFlowInstanceId",workFlowInstanceId);
        WorkFlowNode workFlowNode = workFlowNodeService.selectOne(wrapper);
        model.addAttribute("apply",workFlowNode);
        return "system/apply/edit";
    }

    /**
     * 下面两个方法是用来接收页面传递过来的两个不同的参数
     * @param webDataBinder
     */
    @InitBinder("apply")
    public void applyBinder(WebDataBinder webDataBinder){
        webDataBinder.setFieldDefaultPrefix("apply.");
    }
    @InitBinder("workIns")
    public void workInsBinder(WebDataBinder webDataBinder){
        webDataBinder.setFieldDefaultPrefix("workIns.");
    }
}
