package com.vacomall.service;

import com.baomidou.mybatisplus.service.IService;
import com.vacomall.entity.MeetingSchedule;

import java.util.Date;
import java.util.List;

public interface MeetingScheduleService extends IService<MeetingSchedule> {

    /**
     * 查询会议室的一段时间内的使用情况
     * @param romeId
     * @param begin
     * @param end
     * @return
     */
    List<MeetingSchedule> getRomeSchedule(String romeId, Date begin, Date end);

}
