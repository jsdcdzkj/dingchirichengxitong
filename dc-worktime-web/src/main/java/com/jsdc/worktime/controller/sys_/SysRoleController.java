package com.jsdc.worktime.controller.sys_;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.worktime.sys_.SysRole;
import com.jsdc.worktime.sys_.SysUserRole;
import com.jsdc.worktime.service.sys_.SysRoleService;
import com.jsdc.worktime.service.sys_.SysUserRoleService;
import com.jsdc.worktime.service.sys_.SysUserService;
import com.jsdc.worktime.utils.AjaxJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色管理
 */
@RestController
@RequestMapping("/sysRole")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Autowired
    private SysUserService sysUserService ;



    /**
    *角色列表
    * Author wzn
    * Date 2023/3/8 18:01
    */
    @PostMapping("/getList")
    public AjaxJson getList(SysRole sysRole){

        Page<SysRole> pageinfo = sysRoleService.selectRoleList(sysRole);
        return AjaxJson.getSuccessData(pageinfo);
    }



    /**
     * 保存角色
     *
     * @return
     */
    @PostMapping("/saveRole")
    public AjaxJson saveMenu(@RequestBody SysRole sysRole) {
        return sysRoleService.saveRole(sysRole);
    }


    /**
     * 删除角色
     * @return
     */
    @PostMapping("/delRole")
    public AjaxJson delRole(String roleId) {
        SysRole sysRole = sysRoleService.getById(roleId);
        if(sysRole != null){
            sysRole.setIs_del("1");
            sysRoleService.updateById(sysRole);
        }else {
            return AjaxJson.getError("无此角色！");
        }
        return AjaxJson.getSuccess();
    }


    /**
     * 获取所有角色
     * @return
     */
    @PostMapping("/getAllRoles")
    public AjaxJson getAllRoles(){
        List<SysRole> sysRoles = sysRoleService.list(new QueryWrapper<SysRole>().eq("is_del","0"));
        return AjaxJson.getSuccessData(sysRoles);
    }



    /**
     * 根据用户查询角色
     * @param userId
     * @return
     */
    @PostMapping("/getRolesByUserId")
    public AjaxJson getRolesByUserId(String userId){
        List<SysUserRole> sysUserRoles = sysUserRoleService.list(new QueryWrapper<SysUserRole>().eq("user_id",userId));
        List<Integer> roleIds = new ArrayList<>();
        sysUserRoles.forEach(x->roleIds.add(x.getRole_id()));
        return AjaxJson.getSuccessData(roleIds);
    }


    /**
    *角色树形图数据回显
    * Author wzn
    * Date 2023/3/14 15:34
    */
    @PostMapping("/getRoleList")
    public AjaxJson getRoleList(String roleId){
        return AjaxJson.getSuccessData(sysUserRoleService.getRoleSymbolByRole(roleId) );
    }
}
