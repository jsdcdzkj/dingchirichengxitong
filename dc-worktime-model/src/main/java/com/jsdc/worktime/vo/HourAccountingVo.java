package com.jsdc.worktime.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HourAccountingVo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 项目名称
     */
    private String projectName;
    /**
     * 阶段-版本
     */
    private String iteration_name;
    /**
     * 来源计划
     */
    private String plan_title;
    /**
     * 任务名称
     */
    private String title;
    /**
     * 任务负责人
     */
    private String realName;
    /**
     * 任务属性
     */
    private Integer task_attribute;
    /**
     * 任务类型
     */
    private String task_attribute_name;
    /**
     * 状态：1待指派/2待开始/3进行中/4已结束/5已挂起/6已暂停
     */
    private String status;
    /**
     * 完成情况 状态名称
     */
    private String statusName;
    /**
     * 开始时间
     */
    private String start_time;
    /**
     * 结束时间
     */
    private String completion_time;
    /**
     * 目录-菜单
     */
    private String moduleName;
    /**
     * 功能名称
     */
    private String feature_name;
    /**
     * 运维和实施 任务类型
     */
    private String implementTypeName;
}
