package com.vacomall.service;

import com.baomidou.mybatisplus.service.IService;
import com.vacomall.entity.Dict;

import java.util.List;

public interface DictService extends IService<Dict> {

    /**
     * 根据dictType的值获取字典项
     * @param typeCode
     * @return
     */
    List<Dict> findByTypeCode(String typeCode);

    /**
     * 根据名称统计
     * @param name
     * @return
     */
    int countByName(String name,String typeValue,String id);

    /**
     * 根据value统计
     * @param value
     * @return
     */
    int countByValue(String value,String typeValue,String id);

    /**
     * 根据字典类型值和字典项值进行模糊查询
     * @param typeValue
     * @param value
     * @return
     */
    List<Dict> selectList(String typeValue,String value);

}
