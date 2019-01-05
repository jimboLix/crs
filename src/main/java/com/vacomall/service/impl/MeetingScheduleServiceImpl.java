package com.vacomall.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.vacomall.entity.MeetingSchedule;
import com.vacomall.mapper.MeetingScheduleMapper;
import com.vacomall.service.MeetingScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Service
public class MeetingScheduleServiceImpl extends ServiceImpl<MeetingScheduleMapper, MeetingSchedule> implements MeetingScheduleService {
    @Autowired
    private MeetingScheduleMapper meetingScheduleMapper;
    @Override
    public List<MeetingSchedule> getRomeSchedule(String romeId, Date begin, Date end) {
        Wrapper<MeetingSchedule> wrapper = new EntityWrapper<>();
        if(!StringUtils.isEmpty(romeId) && begin != null && null != end){
            wrapper.addFilter("romeId={0}",romeId);
            wrapper.between("beginTime",begin,end).between("endTime",begin,end);
            List<MeetingSchedule> meetingSchedules = meetingScheduleMapper.selectList(wrapper);
            return meetingSchedules;
        }
        return null;
    }
}
