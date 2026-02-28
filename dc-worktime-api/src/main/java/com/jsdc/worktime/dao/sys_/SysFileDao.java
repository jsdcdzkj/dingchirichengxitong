package com.jsdc.worktime.dao.sys_;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.worktime.sys_.SysFile;
import org.springframework.stereotype.Repository;

@Repository
public class SysFileDao {

    public String selectPageList1(Page page, SysFile bean) {
        StringBuilder sql = new StringBuilder();
        if (bean.getBizType().equals("2")) {
            sql.append("SELECT sf.* FROM pro_project pp INNER JOIN sys_file sf ON pp.id = sf.bizId ");
            sql.append("WHERE sf.bizType = 2 AND pp.id = ").append(bean.getProjectId());
        }
        if (bean.getBizType().equals("3")) {
            sql.append("SELECT sf.* FROM pro_deliverable pd INNER JOIN sys_file sf ON pd.id = sf.bizId INNER JOIN pro_project pp ON pd.projectId = pp.id ");
            sql.append("where sf.bizType = 3 AND pd.projectId = ").append(bean.getProjectId());
        }
        if (bean.getBizType().equals("8")) {
            sql.append("SELECT sf.* FROM task_info ti INNER JOIN sys_file sf ON ti.id = sf.bizId INNER JOIN pro_project pp ON ti.project_id = pp.id  ");
            sql.append("WHERE sf.bizType = 8 AND ti.project_id = ").append(bean.getProjectId());
        }
        if (bean.getBizType().equals("9")) {
            sql.append("SELECT sf.* FROM optimize_suggestion os INNER JOIN sys_file sf ON os.id = sf.bizId INNER JOIN pro_project pp ON os.pro_project_id = pp.id  ");
            sql.append("WHERE sf.bizType = 9 AND os.pro_project_id = ").append(bean.getProjectId());
        }
        if (bean.getBizType().equals("11")) {
            sql.append("SELECT sf.* FROM pro_feature pf INNER JOIN sys_file sf ON pf.id = sf.bizId INNER JOIN pro_project pp ON pf.project_id = pp.id  ");
            sql.append("WHERE sf.bizType = 11 AND pf.project_id = ").append(bean.getProjectId());
        }
        if (bean.getBizType().equals("12")) {
            sql.append("SELECT * FROM sys_file sf WHERE sf.bizType = 12  AND sf.projectId =  ").append(bean.getProjectId());
        }
        if (StrUtil.isNotEmpty(bean.getFileName())) {
            sql.append(" AND sf.fileName like '%").append(bean.getFileName()).append("%'");
        }
        if (1 == bean.getSortDate()) {
            sql.append(" ORDER BY uploadTime asc");
        }else {
            sql.append(" ORDER BY uploadTime desc");
        }
        return sql.toString();
    }

}
