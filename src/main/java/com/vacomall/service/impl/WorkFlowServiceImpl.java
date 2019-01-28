package com.vacomall.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.vacomall.entity.WorkFlow;
import com.vacomall.mapper.WorkFlowMapper;
import com.vacomall.service.WorkFlowService;
import org.springframework.stereotype.Service;

@Service
public class WorkFlowServiceImpl extends ServiceImpl<WorkFlowMapper, WorkFlow> implements WorkFlowService {
}
