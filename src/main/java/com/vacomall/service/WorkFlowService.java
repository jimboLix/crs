package com.vacomall.service;

import com.baomidou.mybatisplus.service.IService;
import com.vacomall.entity.WorkFlow;

import java.util.List;
import java.util.Map;

public interface WorkFlowService extends IService<WorkFlow> {

    /**
     * 获取流程详细信息
     * @param id
     * @return
     */
    List<Map> workFlowDetail(String id);
}
