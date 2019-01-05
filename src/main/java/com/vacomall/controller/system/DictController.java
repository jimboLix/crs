package com.vacomall.controller.system;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.vacomall.common.bean.Rest;
import com.vacomall.common.controller.SuperController;
import com.vacomall.entity.Dict;
import com.vacomall.entity.DictType;
import com.vacomall.service.DictService;
import com.vacomall.service.DictTypeService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;


@Controller
@RequestMapping("/system/dict")
public class DictController extends SuperController {

    @Autowired
    private DictService dictService;
    @Autowired
    private DictTypeService dictTypeService;

    /**
     * 字典类型列表
     * @return
     */
    @RequiresPermissions("dictList")
    @RequestMapping("/type/list/{pageNo}")
    public String typeList(String  name,@PathVariable("pageNo") Integer pageNo,Integer pageSize,Model model){
        //构造分页对象
        Page<DictType> page = getPage(pageNo, pageSize);
        Wrapper<DictType> wrapper = new EntityWrapper<DictType>().orderBy("id");
        if(StringUtils.isNotEmpty(name)) {
            wrapper.like("name", name);
        }
        Page<DictType> dictTypePage = dictTypeService.selectPage(page, wrapper);
        model.addAttribute("pageData",dictTypePage);
        model.addAttribute("name",name);
        return "system/dict/typeList";
    }

    /**
     * 根据字典类型值查询对应字典项
     * @param typeValue 字典类型值
     * @param pageNo 第几页
     * @param pageSize  每页显示条数
     * @param model
     * @return
     */
    @RequiresPermissions("dictList")
    @RequestMapping("/list/{pageNo}")
    public String dictList(String typeValue,  String typeName,String name,@PathVariable Integer pageNo, Integer pageSize, Model model){
        Page<Dict> page = getPage(pageNo,pageSize);
        Wrapper<Dict> wrapper = new EntityWrapper<Dict>().orderBy("createTime");
        if(StringUtils.isNotEmpty(typeValue)){
            wrapper.eq("typeValue",typeValue);
        }
        if(StringUtils.isNotEmpty(name)){
            wrapper.like("name",name);
        }
        Page<Dict> dictPage = this.dictService.selectPage(page, wrapper);
        model.addAttribute("pageData",dictPage);
        model.addAttribute("typeValue",typeValue);
        model.addAttribute("typeName",typeName);
        model.addAttribute("name",name);
        return "system/dict/list";
    }

    /**
     * 字典项添加,跳转到添加页面
     *
     * @param model
     * @return
     */
    @RequestMapping("/dict/toAdd")
    @RequiresPermissions("dictAdd")
    public String toAddDict(Model model,String typeValue) {
        Dict dict = new Dict();
        model.addAttribute("dict",dict);
        model.addAttribute("typeValue",typeValue);
        return "system/dict/dictAdd";
    }

    /**
     * 执行添加或更新操作
     *
     * @param dict
     * @return
     */
    @RequiresPermissions(value={"dictAdd","dictEdit"},logical = Logical.OR)
    @RequestMapping("/dict/doAdd")
    @ResponseBody
    public Rest doDictAdd(Dict dict) {
        this.dictService.insertOrUpdate(dict);
        return Rest.ok();
    }

    /**
     * 字典类型添加，跳转到字典类型添加页面
     *
     * @return
     */
    @RequestMapping("/type/toAdd")
    @RequiresPermissions("dictAdd")
    public String toAddDictType(Model model) {
        //初始化一个空的对象
        DictType type = new DictType();
        model.addAttribute("type",type);
        return "system/dict/typeAdd";
    }

    /**
     * 执行字典类型添加
     *
     * @param dictType
     * @return
     */
    @RequestMapping("/type/doAdd")
    @RequiresPermissions(value = {"dictAdd","dictEdit"},logical = Logical.OR)
    @ResponseBody
    public Rest doDictTypeAdd(DictType dictType) {
        this.dictTypeService.insertOrUpdate(dictType);
        return Rest.ok();
    }

    @ResponseBody
    @RequiresPermissions("dictDel")
    @RequestMapping("/del")
    public Rest dictDel(String id){
        try {
            this.dictService.deleteById(id);
        }catch (Exception e){
            return Rest.failure("系统惊现异常");
        }
        return Rest.ok();
    }

    @ResponseBody
    @RequestMapping("/type/del")
    @RequiresPermissions("dictDel")
    public Rest typeDel(String id){
        try {
            this.dictTypeService.deleteById(id);
        }catch (Exception e){
            return Rest.failure("系统惊现异常");
        }
        return Rest.ok();
    }

    /**
     * 字典类型修改
     * @param id
     * @param model
     * @return
     */
    @RequiresPermissions("dictEdit")
    @RequestMapping("/type/toEdit/{id}")
    public String toTypeEdit(@PathVariable String id,Model model){
        DictType type = this.dictTypeService.selectById(id);
        model.addAttribute("type",type);
        return "system/dict/typeAdd";
    }

    @RequestMapping("/edit/{id}")
    @RequiresPermissions("dictEdit")
    public String toDictEdit(@PathVariable String id,Model model){
        Dict dict = this.dictService.selectById(id);
        model.addAttribute("dict",dict);
        model.addAttribute("typeValue",dict.getTypeValue());
        return "system/dict/dictAdd";
    }

    /**
     * 检查字典类型或者字典项名称是否重复
     *
     * @param checkType 判断是检查字典类型还是字典项的名称
     * @param name      待检查的名称
     * @return
     */
    @RequestMapping("/check/name")
    @ResponseBody
    public Rest checkName(String checkType, String name,String id,String typeValue) {
        //非空判断，为空则不进行检查
        if (StringUtils.isNotEmpty(checkType) && StringUtils.isNotEmpty(name)) {
            int count = 0;
            //checkType是type的话则表明是验证字典类型的名称,否则进行字典项名称的验证
            if ("type".equalsIgnoreCase(checkType)) {
                count = this.dictTypeService.countByName(StringUtils.trim(name),id);
            } else {
                if(StringUtils.isNotEmpty(typeValue)) {
                    count = this.dictService.countByName(name, typeValue,id);
                }
            }

            if (count > 0) {
                return Rest.failure("名称" + name + "重复！");
            }
        }
        return Rest.ok();
    }

    /**
     * 检查字典项或者字典类型的字典值是否重复
     * @param checkType
     * @param value
     * @return
     */
    @RequestMapping("/check/value")
    @ResponseBody
    public Rest checkValue(String checkType, String value,String id,String typeValue) {
        //非空判断，为空则不进行检查
        if (StringUtils.isNotEmpty(checkType) && StringUtils.isNotEmpty(value)) {
            int count = 0;
            //checkType是type的话则表明是验证字典类型的名称,否则进行字典项名称的验证
            if ("type".equalsIgnoreCase(checkType)) {
                count = this.dictTypeService.countByValue(StringUtils.trim(value),id);
            } else {
                if(StringUtils.isNotEmpty(typeValue)) {
                    count = this.dictService.countByValue(value, typeValue,id);
                }
            }

            if (count > 0) {
                return Rest.failure("字典值" + value + "重复！");
            }
        }
        return Rest.ok();
    }

    /**
     * 获取校区对应的教学楼
     * @param pid
     * @return
     */
    @ResponseBody
    @RequestMapping("/getBuild")
    public Rest getBuild(String pid){
        List<Map<String, String>> listMap = new ArrayList<Map<String,String>>();
        if(StringUtils.isNotEmpty(pid)){
            pid = pid +"-";
            List<Dict> dicts = this.dictService.selectList("0011", pid);
            if(!CollectionUtils.isEmpty(dicts)){
                for (Dict dict : dicts){
                    Map<String,String> map = new HashMap<>();
                    map.put("id",dict.getName());
                    map.put("text",dict.getName());
                    listMap.add(map);
                }
            }else{
                Map<String,String> map = new HashMap<>();
                map.put("id","");
                map.put("text","--请选择--");
                listMap.add(map);
            }
            return Rest.okData(listMap);
        }
        return Rest.okData(listMap);
    }
}
