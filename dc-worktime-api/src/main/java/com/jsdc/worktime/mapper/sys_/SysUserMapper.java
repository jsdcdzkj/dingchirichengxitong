package com.jsdc.worktime.mapper.sys_;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsdc.worktime.dao.sys_.SysUserDao;
import com.jsdc.worktime.sys_.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    @SelectProvider(type = SysUserDao.class, method = "selectByGroupId")
    List<SysUser> selectByGroupId(SysUser sysUser);
}
