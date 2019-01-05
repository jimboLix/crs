package com.vacomall.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.vacomall.entity.MeetingRome;
import com.vacomall.service.RomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 首页控制器
* @ClassName: IndexController
* @date 2018年12月31日
*
 */
@Controller
@RequestMapping("/")
public class IndexController {
    @Autowired
    private RomeService romeService;
	
    @RequestMapping({"index"})
    public  String index(){
		return "index";
    }

    @RequestMapping({"","/","/welcome"})
    public String welcome(Model model){
        Wrapper wrapper =  new EntityWrapper<MeetingRome>().addFilter("1=1");
        //如果查询条件不为空
        int count = romeService.selectCount(wrapper);
        List<MeetingRome> romeList = romeService.selectList(wrapper);
        model.addAttribute("count",count);
        model.addAttribute("romeList",romeList);
        return "welcome";
    }
}
