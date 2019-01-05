package com.vacomall.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.vacomall.entity.MeetingRome;
import com.vacomall.mapper.RomeMapper;
import com.vacomall.service.RomeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RomeServiceImpl extends ServiceImpl<RomeMapper, MeetingRome> implements RomeService {


    /**
     * 保存
     * @param rome
     * @return
     */
    @Transactional
    @Override
    public boolean save(MeetingRome rome) {
        if(null != rome){
            //教室编号必填
            if(StringUtils.isBlank(rome.getNo())){
                return false;
            }
            //不允许教室编号重复
            EntityWrapper<MeetingRome> wrapper = new EntityWrapper<MeetingRome>();
            wrapper.eq("no",rome.getNo());
            int count = this.selectCount(wrapper);
            //数据库中查询到编号为rome.getNo()的教室说明重复
            if(count > 0){
                return false;
            }

            return this.insert(rome);
        }
        return false;
    }

    @Override
    public int checkNo(String no, String id) {
        Wrapper<MeetingRome> wrapper = new EntityWrapper<MeetingRome>();
        if(StringUtils.isNotEmpty(no)){
            wrapper.eq("no",no);
        }
        if(StringUtils.isNotEmpty(id)){
            wrapper.addFilter("id !={0}",id);
        }
        List<MeetingRome> meetingRomes = this.selectList(wrapper);
        return this.selectCount(wrapper);
    }
}
