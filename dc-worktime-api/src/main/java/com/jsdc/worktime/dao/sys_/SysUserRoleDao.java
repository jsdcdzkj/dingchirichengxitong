package com.jsdc.worktime.dao.sys_;

import org.springframework.stereotype.Repository;

@Repository
public class SysUserRoleDao {
    public String getRoleSymbolByUser(String userId){
        String sql = "SELECT DISTINCT\n" +
                "\tmenu_id\n" +
                "FROM\n" +
                "\tsys_role_menu srm\n" +
                "LEFT JOIN sys_role sr ON sr.id = srm.role_id\n" +
                "WHERE\n" +
                "\tsrm.role_id IN (\n" +
                "\t\tSELECT\n" +
                "\t\t\trole_id\n" +
                "\t\tFROM\n" +
                "\t\t\tsys_user_role sur\n" +
                "\t\tWHERE\n" +
                "\t\t\tsur.user_id = "+userId+"\n" +
                "\t)";
        return sql;
    }


    public String getRoleSymbolByRole(String roleId){
        String sql = "SELECT DISTINCT\n" +
                "\tmenu_id\n" +
                "FROM\n" +
                "\tsys_role_menu srm\n" +
                "LEFT JOIN sys_role sr ON sr.id = srm.role_id\n" +
                "WHERE\n" +
                "\tsrm.role_id  = "+roleId+"";
        return sql;
    }
}
