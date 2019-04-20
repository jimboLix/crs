package com.vacomall.controller.business;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.vacomall.common.anno.Log;
import com.vacomall.common.bean.Rest;
import com.vacomall.common.controller.SuperController;
import com.vacomall.entity.Dict;
import com.vacomall.entity.MeetingRome;
import com.vacomall.service.DictService;
import com.vacomall.service.RomeService;
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

import java.util.List;

/**
 * 会议室控制层
 */
@Controller
@RequestMapping("/rome")
public class RomeController extends SuperController {

    @Autowired
    private RomeService romeService;
    @Autowired
    private DictService dictService;

    /**
     * 分页查询
     * @param name 查询条件，会议室名称
     * @param no 查询条件，会议室编号
     * @param location 查询条件，会议室位置
     * @param campus 查询条件，会议室所属校区
     * @param volume 查询条件，会议室容量
     * @param pageNo 分页条件，第几页
     * @param pageSize 分页条件，每页显示条数
     * @param model
     * @return
     */
    @RequestMapping("/list/{pageNo}")
    public String getByPage(String name, String no, String location, String campus, Integer volume,String build, @PathVariable Integer pageNo, Integer pageSize, Model model){
        if(null == pageNo || pageNo.equals(0)){
            pageNo = 1;
        }
        if(null == pageSize || pageNo.equals(0)){
            pageSize = 10;
        }
        Page<MeetingRome> page = getPage(pageNo, pageSize);
        //创造查询的wrapper，是mybatis plus的对象
        EntityWrapper<MeetingRome> wrapper = new EntityWrapper<MeetingRome>();
        if(StringUtils.isNotEmpty(name)){
            wrapper.like("name",name);
            model.addAttribute("name",name);
        }
        if(StringUtils.isNotEmpty(no)){
            wrapper.like("no",no);
            model.addAttribute("no",no);
        }

        if(StringUtils.isNotEmpty(location)){
            wrapper.like("location",location);
            model.addAttribute("location",location);
        }

        if(StringUtils.isNotEmpty(campus)){
            wrapper.like("campus",campus);
            model.addAttribute("campus",campus);
            List<Dict> list = this.dictService.selectList(new EntityWrapper<Dict>().eq("name", campus));
            if(!CollectionUtils.isEmpty(list)){
                campus = list.get(0).getValue();
            }
            List<Dict> builds = this.dictService.selectList("0011",campus+"-");
            model.addAttribute("builds",builds);
        }

        if(null != volume && !volume.equals(0)){
            wrapper.eq("volume",volume);
            model.addAttribute("volume",volume);
        }
        if(StringUtils.isNotEmpty(build)){
            wrapper.like("building",build);
            model.addAttribute("build",build);
        }
        Page<MeetingRome> romeList = romeService.selectPage(page, wrapper);
        List<Dict> dictList = this.dictService.findByTypeCode("0010");
//        //如果查询到的会议室数量为0，则根据会议室容量按一定范围查询
//        if(romeList.getTotal()== 0 && null != volume && !volume.equals(0)){
//            Integer maxVolume = volume + 20;
//            Integer minVolume = volume - 20;
//            //或的关系，就是会议室容量等于指定人数或者会议室容量少于指定容量20人或者大于指定容量20人的范围内
//            wrapper.or().between("volume", minVolume, maxVolume);
//            romeList = romeService.selectPage(page,wrapper);
//        }
        model.addAttribute("dictList",dictList);
        model.addAttribute("pageData",romeList);
        return "system/rome/list";
    }


    /**
     * 录入会议室信息
     */
    @RequiresPermissions(value={"romeAdd"})
    @RequestMapping("/add")
    public  String add(Model model){
        MeetingRome rome = new MeetingRome();
        model.addAttribute("rome",rome);
        List<Dict> dictList = this.dictService.findByTypeCode("0010");
        model.addAttribute("dictList",dictList);
        List<Dict> builds = this.dictService.findByTypeCode("0011");
        model.addAttribute("builds",builds);
        return "system/rome/add";
    }

    /**
     * 执行录入会议室信息
     */
    @RequiresPermissions(value={"romeAdd","romeEdit"},logical = Logical.OR)
    @Log("录入会议室信息")
    @RequestMapping("/doAdd")
    @ResponseBody
    public Rest doAdd(MeetingRome rome){
        romeService.insertOrUpdate(rome);
        return Rest.ok();
    }

    @RequestMapping("/checkNo")
    @ResponseBody
    public Rest checkNo(String no,String id){
        int count = this.romeService.checkNo(no, id);
        if(count > 0){
            return Rest.failure("会议室编号"+no+"重复！");
        }
        return Rest.ok();
    }

    @RequiresPermissions("romeDel")
    @RequestMapping("/del/{id}")
    @ResponseBody
    public Rest del(@PathVariable String id){
        if(StringUtils.isNotEmpty(id)) {
            try {
                this.romeService.deleteById(id);
            }catch (Exception e){
                return Rest.failure("删除失败");
            }
        }
        return Rest.ok();
    }

    @RequestMapping("/edit/{id}")
    @RequiresPermissions("romeEdit")
    public String update(@PathVariable String id,Model model){
        MeetingRome meetingRome = this.romeService.selectById(id);
        model.addAttribute("rome",meetingRome);
        List<Dict> dictList = this.dictService.findByTypeCode("0010");
        model.addAttribute("dictList",dictList);
        String campus = meetingRome.getCampus();
        if(StringUtils.isNotEmpty(campus)) {
            List<Dict> list = this.dictService.selectList(new EntityWrapper<Dict>().eq("name", campus));
            if (!CollectionUtils.isEmpty(list)) {
                campus = list.get(0).getValue();
            }
            List<Dict> builds = this.dictService.selectList("0011", campus + "-");
            model.addAttribute("builds", builds);
        }
        return "system/rome/add";
    }
}
