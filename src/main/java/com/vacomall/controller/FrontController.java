package com.vacomall.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.vacomall.entity.MeetingRome;
import com.vacomall.entity.MeetingSchedule;
import com.vacomall.service.MeetingScheduleService;
import com.vacomall.service.RomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

@RequestMapping("/front")
@Controller
public class FrontController {

    @Autowired
    private RomeService romeService;
    @Autowired
    private MeetingScheduleService meetingScheduleService;

    @RequestMapping("/timetable")
    public String front() {
        return "front/timetable";
    }

    /**
     * 'id' => $row['id'],//事件id
     * 'title' => $row['title'],//事件标题
     * 'startDate' => date('Y-m-d H:i',$row['starttime']),//事件开始时间
     * 'endDate' => date('Y-m-d H:i',$row['endtime']),//结束时间
     *
     * @return 2019-11-26
     */
    @RequestMapping("/data")
    @ResponseBody
    public List<Map<String, String>> getData(String start, String end, String romeId) throws Exception {
        List<Map<String, String>> resultList = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        Format f = new SimpleDateFormat("yyyy-MM-dd");
        Format tf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date startDate = ((SimpleDateFormat) f).parse(start);
        Date endDate = ((SimpleDateFormat) f).parse(end);
        List<MeetingSchedule> scheduleList = meetingScheduleService.getRomeSchedule(romeId, startDate, endDate);
        if (!CollectionUtils.isEmpty(scheduleList)) {
            for (MeetingSchedule schedule : scheduleList) {
                Map<String, String> map = new HashMap<>();
                map.put("id",schedule.getId());
                map.put("title", schedule.getUse());
                map.put("start", tf.format(schedule.getBeginTime()));
                map.put("end", tf.format(schedule.getEndTime()));
                resultList.add(map);
            }
        }
//        while (startDate.getTime() < endDate.getTime()){
//            Map<String,String> map = new HashMap<>();
//            i++;
//            map.put("id",i+"aaa");
//            map.put("title",f.format(startDate)+"Meeting");
//            map.put("start",f.format(startDate)+"T18:00");
//            map.put("end",f.format(startDate)+"T19:30");
//
//        }
//        for ()
        return resultList;
    }

    /**
     * 查询满足条件的所有会议室
     *
     * @param
     * @return
     */
    @RequestMapping("/romeAll")
    public String getAll(Model model) {
        Wrapper wrapper = new EntityWrapper<MeetingRome>().addFilter("1=1");
        //如果查询条件不为空
        int count = romeService.selectCount(wrapper);
        List<MeetingRome> romeList = romeService.selectList(wrapper);
        model.addAttribute("count", count);
        model.addAttribute("romeList", romeList);
        return "";
    }

    @RequestMapping("/romeDetail")
    public String romeDetail(String romeId, Model model,Date date) {
        if (StringUtils.isEmpty(romeId)) {
            return "redirect:/welcome";
        }
        MeetingRome rome = romeService.selectById(romeId);
        model.addAttribute("rome", rome);
        model.addAttribute("romeId", romeId);
        //如果没有指定日期则默认当前日期
        if(null == date){
            date = new Date();
        }
        Format f = new SimpleDateFormat("yyyy-MM-dd");
        model.addAttribute("date",f.format(date));
        return "front/timetable";
    }

    /**
     * 获取本月第一天
     *
     * @return
     */
    public Date getMothBegin() {
        Calendar calendar = Calendar.getInstance();
        Date theDate = calendar.getTime();
        GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
        gcLast.setTime(theDate);
        gcLast.set(Calendar.DAY_OF_MONTH, 1);
        return gcLast.getTime();
    }

    public Date getMothEnd() {
        //获取Calendar
        Calendar calendar = Calendar.getInstance();
        //设置日期为本月最大日期
        calendar.set(Calendar.DATE, calendar.getActualMaximum(calendar.DATE));
        return calendar.getTime();
    }
}
