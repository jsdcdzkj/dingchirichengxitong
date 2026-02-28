package com.jsdc.worktime.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Transient;


@Data
public class TaskVo extends Model<TaskVo> {
    private String title;
    private String realName;
    private String start_time;
    private String completion_time;
    private String status;
    private String priority;
    private String priority_name;
    private String end_time;
    private String end_time_str;
    private Integer task_plan_id;
    private Integer task_info_id;
    private String description;
    private Integer assigned_to_user_id;
    private String task_attribute;
    private String source;

    //状态名称
    @Transient
    @TableField(exist = false)
    private String statusName;

    public void setStatusName() {
        // 状态 (1待指派/2待开始/3进行中/4已完成/5已挂起/6已暂停
        if (StringUtils.isNotEmpty(getStatus())) {
            if (getStatus().equals("1")) {
                statusName = "待指派";
            } else if (getStatus().equals("2")) {
                statusName = "待开始";
            } else if (getStatus().equals("3")) {
                statusName = "进行中";
            } else if (getStatus().equals("4")) {
                statusName = "已完成";
            } else if (getStatus().equals("5")) {
                statusName = "已挂起";
            } else if (getStatus().equals("6")) {
                statusName = "已暂停";
            }
        }
    }


    @Transient
    @TableField(exist = false)
    private Integer pageNo;

    @Transient
    @TableField(exist = false)
    private Integer pageSize;
}
