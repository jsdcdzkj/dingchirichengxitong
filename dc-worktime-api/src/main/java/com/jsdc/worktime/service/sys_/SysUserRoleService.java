package com.jsdc.worktime.service.sys_;

import com.jsdc.worktime.mapper.sys_.SysUserRoleMapper;
import com.jsdc.worktime.sys_.SysUserRole;
import com.jsdc.worktime.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysUserRoleService extends BaseService<SysUserRole> {
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;


    public List<String> getRoleSymbolByUser(String userId){
        return sysUserRoleMapper.getRoleSymbolByUser(userId);
    }

    public List<String> getRoleSymbolByRole(String roleId){
        return sysUserRoleMapper.getRoleSymbolByRole(roleId);
    }

}
