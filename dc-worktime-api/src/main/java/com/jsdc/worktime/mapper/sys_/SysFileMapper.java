package com.jsdc.worktime.mapper.sys_;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.worktime.dao.sys_.SysFileDao;
import com.jsdc.worktime.sys_.SysFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

@Mapper
public interface SysFileMapper extends BaseMapper<SysFile> {

    @SelectProvider(type = SysFileDao.class, method = "selectPageList1")
    Page<SysFile> selectPageList1(Page page, SysFile bean);
}
