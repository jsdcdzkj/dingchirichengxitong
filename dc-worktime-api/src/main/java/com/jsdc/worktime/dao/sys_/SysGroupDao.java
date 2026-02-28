package com.jsdc.worktime.dao.sys_;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.worktime.sys_.SysGroup;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

@Repository
public class SysGroupDao {
    public String queryPage(Page page, SysGroup bean){
        return query(bean);
    }
    public String queryList(SysGroup bean){
        return query(bean);
    }

    public String query(SysGroup bean){
        String sql = "select main.* from sys_group main";
        sql += " where main.is_del = '0'";
        if(bean!=null){

            if(null != bean.getType()){
                sql += " and main.type = '" + bean.getType() + "'";
            }

            if(StringUtils.isNotEmpty(bean.getName())){
                sql += " and main.name like '%" + bean.getName() + "%'";
            }

            if (StringUtils.isNotEmpty(bean.getQuery_create_time_start()) && StringUtils.isNotEmpty(bean.getQuery_create_time_end())) {
                sql += " and main.create_time >= '" + bean.getQuery_create_time_start() + " 00:00:00'";
                sql += " and main.create_time <= '" + bean.getQuery_create_time_end() + " 23:59:59'";
            }


        }
        sql += " order by main.create_time desc";
        return sql;
    }
}

