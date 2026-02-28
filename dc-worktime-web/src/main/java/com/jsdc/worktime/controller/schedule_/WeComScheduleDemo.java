package com.jsdc.worktime.controller.schedule_;

import com.jsdc.worktime.global.ScheduleListResponse;
import com.jsdc.worktime.global.WeComScheduleClient;
import com.jsdc.worktime.schedule_.Schedule;

import java.io.IOException;

public class WeComScheduleDemo {
    // 企业微信配置信息（请替换为实际值）
    private static final String CORPID = "your_corp_id";
    private static final String CORPSECRET = "your_corp_secret";
    private static final String CAL_ID = "your_calendar_id";

    public static void main(String[] args) {
        try {
            // 1. 获取Access Token
            String accessToken = WeComScheduleClient.getAccessToken(CORPID, CORPSECRET);
            System.out.println("Access Token: " + accessToken);

            // 2. 获取日程列表（获取2026-02-27当天的日程）
            String startTime = "2026-02-27T00:00:00+08:00";
            String endTime = "2026-02-27T23:59:59+08:00";
            ScheduleListResponse response = WeComScheduleClient.getScheduleList(
                    accessToken, CAL_ID, startTime, endTime);

            // 3. 处理日程数据
            if (response.getErrcode() == 0) {
                System.out.println("成功获取日程列表，共" + response.getScheduleList().size() + "条记录");
                for (Schedule schedule : response.getScheduleList()) {
                    System.out.println("------------------------------");
                    System.out.println("日程标题: " + schedule.getSummary());
                    System.out.println("开始时间: " + schedule.getStart_time());
                    System.out.println("结束时间: " + schedule.getEnd_time());
                    System.out.println("参与人数: " + schedule.getAttendees().size());
                }
            } else {
                System.err.println("获取日程失败: " + response.getErrmsg());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}