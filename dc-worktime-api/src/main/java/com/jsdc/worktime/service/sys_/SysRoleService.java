package com.jsdc.worktime.service.sys_;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.worktime.mapper.sys_.SysRoleMapper;
import com.jsdc.worktime.mapper.sys_.SysRoleMenuMapper;
import com.jsdc.worktime.sys_.SysRole;
import com.jsdc.worktime.sys_.SysRoleMenu;
import com.jsdc.worktime.sys_.SysUser;
import com.jsdc.worktime.service.BaseService;
import com.jsdc.worktime.utils.AjaxJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
public class SysRoleService extends BaseService<SysRole> {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysRoleMapper sysRoleMapper ;

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper ;


    /**
    *角色新增
    * Author wzn
    * Date 2023/3/8 15:39
    */
    public AjaxJson saveRole(SysRole sysRole){
        SysUser currUser = sysUserService.getUser();
        if(null != sysRole.getId()){
            //修改
            sysRole.setUpdate_user(currUser.getId());
            sysRole.setUpdate_time(new Date());

        }else{
            //新增
            sysRole.setCreate_user(currUser.getId());
            sysRole.setCreate_user_name(currUser.getUsername());
            sysRole.setCreate_time(new Date());
            sysRole.setIs_del("0");
        }
        sysRole.insertOrUpdate();

        //删除旧菜单
        QueryWrapper<SysRoleMenu> queryWrapper = new QueryWrapper<>() ;
        queryWrapper.eq("role_id",sysRole.getId()) ;
        sysRoleMenuMapper.delete(queryWrapper) ;
        //新增菜单
        for (Integer roleId:sysRole.getMenuIds()){
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setRole_id(sysRole.getId());
            sysRoleMenu.setMenu_id(roleId);
            sysRoleMenu.insert();
        }
        return AjaxJson.getSuccess();
    }



    public Page<SysRole> selectRoleList(SysRole sysRole) {
        int pageNo = null == sysRole.getPageNo()? 1 : sysRole.getPageNo();
        int pageSize = null == sysRole.getPageSize()? 10 : sysRole.getPageSize();
        Page<SysRole> page = new Page<>(pageNo, pageSize);
        QueryWrapper<SysRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_del", "0");
        if (!"".equals(sysRole.getRole_name()) && null != sysRole.getRole_name()) {
            queryWrapper.like("role_name", sysRole.getRole_name());
        }

        Page<SysRole> sysRolePage = sysRoleMapper.selectPage(page, queryWrapper);
        return sysRolePage;
    }

}
