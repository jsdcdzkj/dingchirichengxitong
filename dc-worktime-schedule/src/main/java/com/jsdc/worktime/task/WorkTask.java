package com.jsdc.worktime.task;

import cn.hutool.core.date.DateTime;
import com.jsdc.worktime.service.task_.*;
import com.jsdc.worktime.service.test_.TestBugService;
import com.jsdc.worktime.task_.TaskMeetInfo;
import com.jsdc.worktime.task_.TaskMeetUsers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 任务工时
 * 定时任务
 */
@Component
@Slf4j
public class WorkTask {

    @Autowired
    private TaskInfoService taskInfoService;
    @Autowired
    private TaskLeadersInfoService taskLeadersInfoService;
    @Autowired
    private TaskOperationInfoService taskOperationInfoService;
    @Autowired
    private TestBugService testBugService;
    @Autowired
    private TaskMeetUsersService taskMeetUsersService;

    /**
     * 每天12点整执行 任务暂停
     */
    @Scheduled(cron = "0 0 12,23 * * ?")
    public void stopAm() {
        taskInfoService.stopTaskSchedule();
        taskLeadersInfoService.stopTaskSchedule();
        taskOperationInfoService.stopTaskSchedule();
        testBugService.stopTaskSchedule();
        taskMeetUsersService.stopTaskSchedule();

//        System.out.println(new DateTime().toString("yyyy-MM-dd HH:mm:ss") + "任务暂停" +
//                " =============================================");
//        log.error(new DateTime().toString("yyyy-MM-dd HH:mm:ss") + "任务暂停" +
//                " =============================================");
    }

    /**
     * 每天17点30执行 任务暂停
     */
    @Scheduled(cron = "0 30 17 * * ?")
    public void stopPm() {
        taskInfoService.stopTaskSchedule();
        taskLeadersInfoService.stopTaskSchedule();
        taskOperationInfoService.stopTaskSchedule();
        testBugService.stopTaskSchedule();
        taskMeetUsersService.stopTaskSchedule();

//        System.out.println(new DateTime().toString("yyyy-MM-dd HH:mm:ss") + "任务暂停" +
//                " =============================================");
//        log.error(new DateTime().toString("yyyy-MM-dd HH:mm:ss") + "任务暂停" +
//                " =============================================");
    }

    /**
     * 任务快到期，提前15分钟提醒，5分钟执行一次
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void taskSendMessage() {
        taskInfoService.sendMessageTask();
        taskLeadersInfoService.sendMessageTask();
        taskOperationInfoService.sendMessageTask();
        taskMeetUsersService.sendMessageTask();
//        testBugService.stopTaskSchedule();

//        System.out.println(new DateTime().toString("yyyy-MM-dd HH:mm:ss") + "任务快到期，提前15分钟提醒，5分钟执行一次" +
//                " =============================================");
    }


    /**
     * 每天早上8.30~8:40，下午13:30~13:40，如果有待开始的任务，每3分钟提醒一次
     */
    @Scheduled(cron = "0 31,34,37,40 8,13 * * ?")
    public void amStartWorkSendMessage() {
        taskOperationInfoService.startWorkSendMessage();
    }

    /**
     * 每天下午13:30~13:40，如果有待开始的任务，每3分钟提醒一次
     */
//    @Scheduled(cron = "0 31,34,37,41 13 * * ?")
//    public void pmStartWorkSendMessage() {
//        taskOperationInfoService.startWorkSendMessage();
//    }

}
