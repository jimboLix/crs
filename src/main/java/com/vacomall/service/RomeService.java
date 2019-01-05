package com.vacomall.service;

import com.baomidou.mybatisplus.service.IService;
import com.vacomall.entity.MeetingRome;

public interface RomeService extends IService<MeetingRome> {

    boolean save(MeetingRome rome);

    int checkNo(String no,String id);
}
