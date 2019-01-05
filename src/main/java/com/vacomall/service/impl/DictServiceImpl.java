package com.vacomall.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.vacomall.entity.Dict;
import com.vacomall.mapper.DictMapper;
import com.vacomall.service.DictService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {
    @Override
    public List<Dict> findByTypeCode(String typeCode) {
        Wrapper<Dict> wrapper = new EntityWrapper<Dict>();
        wrapper.eq("typeValue", typeCode);
        return this.selectList(wrapper);
    }

    @Override
    public int countByName(String name, String typeValue, String id) {
        Wrapper<Dict> wrapper = new EntityWrapper<Dict>();
        if (StringUtils.isNotEmpty(name)) {
            wrapper.eq("name", name);
        }
        if (StringUtils.isNotEmpty(typeValue)) {
            wrapper.eq("typeValue", typeValue);
        }
        if (StringUtils.isNotEmpty(id)) {
            wrapper.addFilter("id != {0}", id);
        }
        return this.selectCount(wrapper);
    }

    @Override
    public int countByValue(String value, String typeValue, String id) {
        Wrapper<Dict> wrapper = new EntityWrapper<Dict>();
        if (StringUtils.isNotEmpty(value)) {
            wrapper.eq("value", value);
        }
        if (StringUtils.isNotEmpty(typeValue)) {
            wrapper.eq("typeValue", typeValue);
        }
        if (StringUtils.isNotEmpty(id)) {
            wrapper.addFilter("id != {0}", id);
        }
        return this.selectCount(wrapper);
    }

    @Override
    public List<Dict> selectList(String typeValue, String value) {
        Wrapper<Dict> wrapper = new EntityWrapper<Dict>();
        if(StringUtils.isNotEmpty(typeValue)){
            wrapper.eq("typeValue",typeValue);
        }
        if(StringUtils.isNotEmpty(value)){
            wrapper.like("value",value);
        }
        return this.selectList(wrapper);
    }
}
