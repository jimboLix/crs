package com.vacomall.test.service;

import com.vacomall.entity.Dict;
import com.vacomall.entity.DictType;
import com.vacomall.service.DictService;
import com.vacomall.service.DictTypeService;
import com.vacomall.test.SpringTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DictTest extends SpringTest {

    @Autowired
    private DictTypeService dictTypeService;
    @Autowired
    private DictService dictService;

    @Test
    public void testSaveType(){
        DictType type = new DictType();
        type.setName("校区");
        type.setValue("0010");
        type.setDesc("校区字典项管理");
        dictTypeService.insert(type);
    }

    @Test
    public void testSaveDict(){
        Dict dict = new Dict();
        dict.setDesc("南校区");
        dict.setName("会峰校区");
        dict.setTypeValue("0010");
        dict.setValue("1");

        Dict dictN = new Dict();
        dictN.setDesc("北校区");
        dictN.setName("琅琊校区");
        dictN.setTypeValue("0010");
        dictN.setValue("2");
        dictService.insert(dict);
        dictService.insert(dictN);
    }

    @Test
    public void checkValue(){
        int i = this.dictTypeService.countByValue("0010","");
        Assert.assertFalse(true);
    }
}
