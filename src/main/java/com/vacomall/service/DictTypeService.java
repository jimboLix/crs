package com.vacomall.service;

import com.baomidou.mybatisplus.service.IService;
import com.vacomall.entity.DictType;

import java.util.List;

public interface DictTypeService extends IService<DictType> {

    /**
     * 查询所有的字典类型
     * @return
     */
    List<DictType> getAll();

    /**
     * 根据名称进行统计
     * @param name
     * @return
     */
    int countByName(String name,String id);

    /**
     * 根据value进行统计
     *
     */
    int countByValue(String value,String id);
}
