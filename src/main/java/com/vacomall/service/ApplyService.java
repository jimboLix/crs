package com.vacomall.service;

import com.baomidou.mybatisplus.service.IService;
import com.vacomall.common.bean.Rest;
import com.vacomall.entity.Apply;

public interface ApplyService extends IService<Apply> {

    Rest createApply(Apply apply, String workFlowId, String daterange, String createTime,String node);

}
