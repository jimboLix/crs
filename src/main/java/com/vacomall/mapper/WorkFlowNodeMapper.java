package com.vacomall.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.vacomall.entity.WorkFlowNode;
import com.vacomall.entity.vo.WorkFlowNodeVo;

import java.util.List;
import java.util.Map;

public interface WorkFlowNodeMapper extends BaseMapper<WorkFlowNode> {
    List<WorkFlowNodeVo> myTasksPage(Map params, Page page);
}
