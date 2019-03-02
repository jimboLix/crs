package com.vacomall.controller.business;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.vacomall.common.bean.Rest;
import com.vacomall.common.controller.SuperController;
import com.vacomall.entity.SysRole;
import com.vacomall.entity.WorkFlow;
import com.vacomall.entity.vo.Node;
import com.vacomall.entity.vo.WorkFlowVo;
import com.vacomall.service.ISysRoleService;
import com.vacomall.service.WorkFlowService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/workflow")
@Controller
public class WorkFlowController extends SuperController {

   @Autowired
   private WorkFlowService workFlowService;
   @Autowired
   private ISysRoleService roleService;

    @RequestMapping("/list/{pageNo}")
    public String instanceList(Model model, @PathVariable Integer pageNo, Integer pageSize){
        if(null == pageNo){
            pageNo = 1;
        }
        if(pageSize == null){
            pageSize = 10;
        }
        Page<WorkFlow> page = this.getPage(pageNo,pageSize);
        Page<WorkFlow> workFlowPage = workFlowService.selectPage(page, new EntityWrapper<>());
        model.addAttribute("pageData",workFlowPage);
        return "system/workFlow/list";
    }

    @RequestMapping("/detail/{id}")
    public String workFLowDetail(@PathVariable String id,Model model){
        List<Map> maps = this.workFlowService.workFlowDetail(id);
        model.addAttribute("nodes",maps);
        return "system/workFlow/detail";
    }
    /**
     * 动态添加表格,时间线,每点击一次添加节点则发送一次后台请求,创建一个map类型的node,然后传递到前台显示
     */
    @RequestMapping("/add")
    public String add(Model model,String id){
        WorkFlow workFlow = null;
        if(StringUtils.isEmpty(id)) {
            //添加
            workFlow = new WorkFlow();
        }else{
            //编辑
            workFlow = workFlowService.selectById(id);
            List<Map> maps = this.workFlowService.workFlowDetail(id);
            model.addAttribute("nodes",maps);
        }
        //获取所有的角色用于节点进行选择
        List<SysRole> sysRoles = roleService.selectList(null);
        model.addAttribute("roles",sysRoles);
        model.addAttribute("workFlow",workFlow);
        return "system/workFlow/add";
    }

    @RequestMapping("/doAdd")
    @ResponseBody
    public Rest doAdd(WorkFlowVo workFlow){
        WorkFlow flow = new WorkFlow();
        List<Node> nodeList = workFlow.getNodeList();
        String detail = JSON.toJSONString(nodeList);
        BeanUtils.copyProperties(workFlow,flow);
        flow.setWorkFlowDetail(detail);
        this.workFlowService.insertOrUpdate(flow);
        return Rest.ok();
    }
}
