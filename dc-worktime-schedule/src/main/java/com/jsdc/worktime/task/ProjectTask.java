package com.jsdc.worktime.task;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jsdc.worktime.mapper.pro_.ProProjectMapper;
import com.jsdc.worktime.mapper.task_.TaskInfoMapper;
import com.jsdc.worktime.mapper.task_.TaskOperationInfoMapper;
import com.jsdc.worktime.mapper.test_.TestBugMapper;
import com.jsdc.worktime.pro_.ProProject;
import com.jsdc.worktime.service.pro_.ProProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 项目定时任务
 *
 * @author: jxl
 * @create: 2025-03-24 13:44
 **/
@Component
public class ProjectTask {

    @Autowired
    private TaskInfoMapper taskInfoMapper;
    @Autowired
    private TaskOperationInfoMapper taskOperationInfoMapper;
    @Autowired
    private TestBugMapper testBugMapper;
    @Autowired
    private ProProjectMapper projectMapper;
    @Autowired
    private ProProjectService projectService;


    @Scheduled(cron = "0 0 0 * * ?")//每天凌晨执行
//    @Scheduled(cron = "0 0/1 * * * ?")//每分钟一次 测试使用
    public void projectTask() {
        //任务状态通过列表查询的时候做查询判断处理了，此段代码作为托底使用
        List<ProProject> projects = projectMapper.selectList(new LambdaQueryWrapper<ProProject>()
                .eq(ProProject::getIsDel, 0)
                .in(ProProject::getProjectStatus, 1, 2)
        );
        //存在进行中的计划     进行中
        //所有计划都完成  已完成
        if (CollUtil.isNotEmpty(projects)) {
            for (ProProject project : projects) {
                Integer projectId = project.getId();
                Integer notStartNum = projectService.calcTask(projectId, Arrays.asList("1", "2", "5", "6"), Arrays.asList("1", "2", "5", "6"), Arrays.asList("1", "2", "6", "7"));
                Integer underwayNum = projectService.calcTask(projectId, Collections.singletonList("3"), Collections.singletonList("3"), Collections.singletonList("3"));
                Integer finishedNum = projectService.calcTask(projectId, Collections.singletonList("4"), Collections.singletonList("4"), Arrays.asList("4", "5"));
                //所有任务数
                Integer allTaskNum = notStartNum + underwayNum + finishedNum;
                if (allTaskNum.equals(notStartNum)){
                    project.setProjectStatus("1");
                }else if (finishedNum.equals(allTaskNum)){
                    project.setProjectStatus("3");
                }else {
                    project.setProjectStatus("2");
                }
                project.setUpdateTime(new Date());
                projectMapper.updateById(project);
            }
        }
    }

}
