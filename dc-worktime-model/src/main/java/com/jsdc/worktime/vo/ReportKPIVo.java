package com.jsdc.worktime.vo;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

/**
 * 季度绩效考核
 */
@Data
public class ReportKPIVo extends Model<ReportKPIVo> {
    //开始时间
    private String startTime;
    //结束时间
    private String endTime;
    //考核组id
    private Integer team_id;

    //姓名
    private String name;

    private Integer userId;

    //考核组
    private String team_name;

    // BUG率占比/工作计划按时完成率/测试任务按期完成率/按时完成实施运维任务率
    private String rate1;
    //验收一次通过率/设计效果内部评审一次性通过率/项目验收一次性通过率
    private String rate2;

    //测试报告bug覆盖率
    private String rate3;

    // BUG率占比/工作计划按时完成率/测试任务按期完成率/按时完成实施运维任务率 公式
    private String rate1_formula;
    //验收一次通过率/设计效果内部评审一次性通过率/项目验收一次性通过率 公式
    private String rate2_formula;

    //测试报告bug覆盖率 公式
    private String rate3_formula;
}
