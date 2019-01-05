package com.vacomall.test.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.vacomall.entity.MeetingRome;
import com.vacomall.mapper.RomeMapper;
import com.vacomall.service.RomeService;
import com.vacomall.test.SpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RomeServiceTest extends SpringTest {

    @Autowired
    private RomeMapper romeMapper;
    @Autowired
    private RomeService romeService;

    @Test
    public void findTest(){
        Wrapper<MeetingRome> wrapper = new EntityWrapper<MeetingRome>().eq("id","1");
        List<MeetingRome> meetingRomes = romeMapper.selectList(wrapper);
        System.out.println(meetingRomes.size());
    }

    @Test
    public void insertTest(){
        MeetingRome m = new MeetingRome();
        m.setDetail("测试用例");
        m.setCampus("会峰校区");
        m.setFacilities("投影仪");
        m.setLocation("逸夫4号楼4层");
        m.setName("综合会议室");
        m.setNo("YF-4405");
        m.setVolume(100);
        boolean insert = romeService.save(m);
        System.out.println(insert);
    }

    @Test
    public void deleteTest(){
        Map<String,Object> cmp = new HashMap<>();
        cmp.put("id","ffb12d17aecd4a4d9b8c8deaf0cdb9cb");
        boolean b = this.romeService.deleteByMap(cmp);
        System.out.println(b);
    }
}
