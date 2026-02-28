package com.jsdc.worktime.global;

import com.jsdc.worktime.schedule_.Schedule;

import java.util.List;

// 日程列表响应实体
public class ScheduleListResponse {
    private int errcode;
    private String errmsg;
    private List<Schedule> schedule_list;

    // Getters and Setters
    public int getErrcode() { return errcode; }
    public void setErrcode(int errcode) { this.errcode = errcode; }
    public String getErrmsg() { return errmsg; }
    public void setErrmsg(String errmsg) { this.errmsg = errmsg; }
    public List<Schedule> getScheduleList() { return schedule_list; }
    public void setScheduleList(List<Schedule> schedule_list) { this.schedule_list = schedule_list; }
}