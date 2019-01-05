package com.vacomall.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.vacomall.entity.DictType;
import com.vacomall.mapper.DictTypeMapper;
import com.vacomall.service.DictTypeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DictTypeServiceImpl extends ServiceImpl<DictTypeMapper, DictType> implements DictTypeService {

    @Override
    public List<DictType> getAll() {
        Wrapper<DictType> wrapper = new EntityWrapper<DictType>().addFilter("id !={0}","-1");
        List<DictType> typeList = this.selectList(wrapper);
        return typeList;
    }

    @Override
    public int countByName(String name,String id) {
        Wrapper<DictType> wrapper = new EntityWrapper<DictType>();
        if(StringUtils.isNotEmpty(name)){
            wrapper.eq("name",name);
        }
        if(StringUtils.isNotEmpty(id)){
            wrapper.addFilter("id !={0}",id);
        }
        return this.selectCount(wrapper);
    }

    @Override
    public int countByValue(String value,String id) {
        Wrapper<DictType> wrapper = new EntityWrapper<DictType>();
        if(StringUtils.isNotEmpty(value)){
            wrapper.eq("value",value);
        }
        if(StringUtils.isNotEmpty(id)){
            wrapper.addFilter("id !={0}",id);
        }
        return this.selectCount(wrapper);
    }
}
