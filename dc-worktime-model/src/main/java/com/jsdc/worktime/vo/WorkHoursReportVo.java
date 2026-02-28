package com.jsdc.worktime.vo;

import lombok.Data;

import java.util.List;


@Data
public class WorkHoursReportVo {
    private List<Integer> projectIds;
    //项目ID
    private Integer project_id;

    //组ID
    private Integer groupId;

    private Integer user_id;

    //预估工时（人天）
    private String planing_hours;

    //实际工时（人天）
    private String working_hours;

    //超时工时（人天）
    private String diff_hours;

    //项目开发成本（元）
    private String project_cost;

    //名称
    private String name;
    //值
    private String value;
    //任务工时
    private String sumWorkHoursTask;
    //运维工时
    private String sumWorkHoursOperation;
    //BUG修复工时
    private String sumWorkHoursTestBug;
    //领导交办工时
    private String sumWorkHoursLeaders;
    //合计工时
    private String sumWorkHoursTotal;
    //查询时间
    private String search_time;
    //查询任务完成时间
    private String completion_start_time;
    //查询任务完成时间
    private String completion_end_time;
    //查询开始时间
    private String start_time;
    //查询结束时间
    private String end_time;

    //人员集合
    private List<Integer> userIdList;
    //任务状态集合
    private List<String> statusList;
    //人员名称
    private String realName;
    //项目名称
    private String projectName;

    //计划任务数量
    private String countTask;
    //运维任务数量
    private String countOperation;
    //BUG修复任务数量
    private String countTestBug;
    //领导交办任务数量
    private String countLeaders;
    //会议任务数量
    private String countMeet;
    //任务优先数量
    private String countPriority;

    private List<WorkHoursReportVo> reportVoList;

    //预估工时 XX天XX时XX分
    private String planDays;
}
