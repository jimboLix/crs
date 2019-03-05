package com.vacomall.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.vacomall.service.ISysUserRoleService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.vacomall.common.util.ShiroUtil;
import com.vacomall.entity.SysUser;
import com.vacomall.entity.SysUserRole;
import com.vacomall.mapper.SysUserMapper;
import com.vacomall.mapper.SysUserRoleMapper;
import com.vacomall.service.ISysUserService;

/**
 *
 * SysUser 表数据服务层接口实现类
 *
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

	@Autowired private SysUserMapper userMapper;
	
	@Autowired private SysUserRoleMapper userRoleMapper;
	@Autowired
	private ISysUserRoleService userRoleService;
	
	@Override
	public void insertUser(SysUser user, String[] roleIds) {
		// TODO Auto-generated method stub
		user.setCreateTime(new Date());
    	user.setPassword(ShiroUtil.md51024Pwd(user.getPassword(), user.getUserName()));
		//保存用户
    	userMapper.insert(user);
		//绑定角色
		if(ArrayUtils.isNotEmpty(roleIds)){
			for(String rid : roleIds){
				SysUserRole sysUserRole = new SysUserRole();
				sysUserRole.setUserId(user.getId());
				sysUserRole.setRoleId(rid);
				userRoleMapper.insert(sysUserRole);
			}
		}
		
	}

	@Override
	public void updateUser(SysUser sysUser, String[] roleIds) {
		// TODO Auto-generated method stub
		//如果前端传来的密码不是空则说明用户修改了密码需要进行更新
		if(StringUtils.isNotEmpty(sysUser.getPassword())){
			sysUser.setPassword(ShiroUtil.md51024Pwd(sysUser.getPassword(), sysUser.getUserName()));
		}else{
			//否则进行密码保持不变
			SysUser user = userMapper.selectById(sysUser.getId());
			sysUser.setPassword(user.getPassword());
		}
		//更新用户
		userMapper.updateById(sysUser);
		//删除已有权限
		userRoleMapper.delete(new EntityWrapper<SysUserRole>().eq("userId",sysUser.getId()));
		//重新绑定角色
		if(ArrayUtils.isNotEmpty(roleIds)){
			for(String rid : roleIds){
				SysUserRole sysUserRole = new SysUserRole();
				sysUserRole.setUserId(sysUser.getId());
				sysUserRole.setRoleId(rid);
				userRoleMapper.insert(sysUserRole);
			}
		}
	}

	@Override
	public Page<Map<Object, Object>> selectUserPage(Page<Map<Object, Object>> page, String search) {
		// TODO Auto-generated method stub
		page.setRecords(baseMapper.selectUserList(page, search));
		return page;
	}

	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub
		this.deleteById(id);
		userRoleMapper.delete(new EntityWrapper<SysUserRole>().addFilter("userId = {0}", id));
	}

	@Override
	public List<SysUser> getUsersByRole(String roleId) {
		List<SysUser> userList = new ArrayList<>();
		try{
			userList = userMapper.getUsersByRole(roleId);
		}catch (Exception e){
			e.printStackTrace();
		}
		return userList;
	}


}