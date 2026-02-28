package com.jsdc.worktime.dao.sys_;

import com.jsdc.worktime.sys_.SysUser;
import org.springframework.stereotype.Repository;

@Repository
public class SysUserDao {

    public String selectByGroupId(SysUser bean) {
        String sql = "SELECT main.*,\n" +
                "       gu.groupId\n" +
                "FROM `sys_user` main\n" +
                "         LEFT JOIN sys_group_user gu ON gu.userId = main.id\n";
        sql += "WHERE main.is_del = 0";
        if(bean != null){
            if(null != bean.getStatus()){
                sql += " AND main.status = '" + bean.getStatus().toString() + "'";
            }
            if(null != bean.getGroupId()){
                sql += " AND gu.groupId = '" + bean.getGroupId() + "'";
            }else {
                sql += " AND main.groupId = '8'";
            }
        }
        return sql;
    }
}
