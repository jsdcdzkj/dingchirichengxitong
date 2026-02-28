package com.jsdc.worktime.vo;

import lombok.Data;

/**
 * 工作数据
 */
@Data
public class StatisticsUserVo {

    // TODO 工作时长分析
    /** 预估工作时长 */
    private String planHours;
    /** 任务工作时长 */
    private String taskHours;
    /** BUG修复工作时长 */
    private String bugHours;
    // TODO 工作内容分析
    /** 挑战任务数 */
    private Integer challengeCount;
    /** 困难任务数 */
    private Integer difficultCount;
    /** 正常任务数 */
    private Integer normalCount;
    /** 简单任务数 */
    private Integer easyCount;
    /** 轻松任务数 */
    private Integer lightCount;
    /** 挑战功能数 */
    private Integer challengeFun;
    /** 困难功能数 */
    private Integer difficultFun;
    /** 正常及以下功能数 */
    private Integer normalFun;
    // TODO 工作质量分析
    /** BUG率占比 */
    private String bugRate;
    /** 需求 验收一次通过率 */
    private String passRate;
    /** 挑战功能bug率 */
    private String challengeBugRate;
    /** 困难功能bug率 */
    private String difficultBugRate;
    /** 正常及以下功能bug率 */
    private String normalBugRate;

    /** 活跃项目数 */
    private Integer activeProjectCount;
    /** 完成功能数 */
    private Integer completeFunCount;
    /** 领导交办任务打回率 */
    private String leadBackRate;
    /** 领导交办任务平均分 */
    private String taskScoreAvg;
    /** 测试任务按期完成率 */
    private String testTaskRate;
    /** 测试报告bug覆盖率 */
    private String testBugRate;
    /** 评审通过率 */
    private String milestonePassRate;


    // TODO 工作时限分析
    /** 提前完成任务数 */
    private Integer earlyCount;
    /** 准时完成任务数 */
    private Integer onTimeCount;
    /** 延迟完成任务数 */
    private Integer delayCount;
    /** 按实开展项目评审率 */
    private String projectReviewRate;




}
