package com.jsdc.worktime.mapper.sys_;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.worktime.dao.sys_.SysGroupDao;
import com.jsdc.worktime.sys_.SysGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface SysGroupMapper extends BaseMapper<SysGroup> {
    @SelectProvider(type = SysGroupDao.class, method = "queryPage")
    Page<SysGroup> queryPage(Page page, SysGroup bean);
    @SelectProvider(type = SysGroupDao.class, method = "queryList")
    List<SysGroup> queryList(SysGroup bean);
}

