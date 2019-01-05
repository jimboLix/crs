package com.vacomall.test.service;

import com.vacomall.entity.MeetingSchedule;
import com.vacomall.service.MeetingScheduleService;
import com.vacomall.test.SpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MeetingScheduleServiceTest extends SpringTest {
    @Autowired
    private MeetingScheduleService meetingScheduleService;

    @Test
    public void initData() throws Exception {
        Date start = this.getMothBegin();
        Date end = this.getMothEnd();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        while (start.getTime() < end.getTime()){
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(start);
            calendar.add(calendar.DATE,1);
            MeetingSchedule schedule = new MeetingSchedule();
            schedule.setRomeId("a1ceedaea0f643a1b1fa108abae77f9f");
            String format = df.format(start);
            String sformat = format +" 10:00";
            String eformat = format+" 12:00";
            Date startTime = f.parse(sformat);
            schedule.setBeginTime(startTime);
            Date emdTime = f.parse(eformat);
            schedule.setEndTime(emdTime);
            schedule.setUse("开会测试项目");
            schedule.setDetail("这是个测试");
            schedule.setApplyid("sasasas");
            meetingScheduleService.insert(schedule);
            start = calendar.getTime();
        }
    }

    /**
     * 获取本月第一天
     *
     * @return
     */
    private Date getMothBegin() {
        Calendar calendar = Calendar.getInstance();
        Date theDate = calendar.getTime();
        GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
        gcLast.setTime(theDate);
        gcLast.set(Calendar.DAY_OF_MONTH, 1);
        return gcLast.getTime();
    }

    private Date getMothEnd() {
        //获取Calendar
        Calendar calendar = Calendar.getInstance();
        //设置日期为本月最大日期
        calendar.set(Calendar.DATE, calendar.getActualMaximum(calendar.DATE));
        return calendar.getTime();
    }
}
