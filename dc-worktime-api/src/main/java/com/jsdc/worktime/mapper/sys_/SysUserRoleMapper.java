package com.jsdc.worktime.mapper.sys_;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsdc.worktime.dao.sys_.SysUserRoleDao;
import com.jsdc.worktime.sys_.SysUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
    @SelectProvider(type= SysUserRoleDao.class,method = "getRoleSymbolByUser")
    List<String> getRoleSymbolByUser(String userId);

    @SelectProvider(type= SysUserRoleDao.class,method = "getRoleSymbolByRole")
    List<String> getRoleSymbolByRole(String roleId);


}
