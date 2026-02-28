package com.jsdc.worktime.task;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jsdc.worktime.global.G;
import com.jsdc.worktime.mapper.pro_.ProPlanMapper;
import com.jsdc.worktime.mapper.task_.TaskInfoMapper;
import com.jsdc.worktime.mapper.task_.TaskPlanMapper;
import com.jsdc.worktime.pro_.ProPlan;
import com.jsdc.worktime.task_.TaskInfo;
import com.jsdc.worktime.task_.TaskPlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * 项目定时任务
 *
 * @author: jxl
 * @create: 2025-03-24 13:44
 **/
@Component
public class ProPlanTask {

    @Autowired
    private ProPlanMapper planMapper;
    @Autowired
    private TaskPlanMapper taskPlanMapper;
    @Autowired
    private TaskInfoMapper taskInfoMapper;


    @Scheduled(cron = "0 0/3 * * * ?")
    public void projectTask() {
        // 合并处理两种计划类型（顺序保持先3后2）
        processPlansByType(3, this::updateTaskDrivenPlanStatus);
        processPlansByType(2, this::updateHierarchyDrivenPlanStatus);
        processPlansByType(1, this::updateHierarchyDrivenPlanStatus);
    }

    /**
     * 通用计划处理方法
     * @param planType 计划类型
     * @param statusUpdater 状态更新策略函数
     */
    private void processPlansByType(int planType, BiConsumer<ProPlan, List<?>> statusUpdater) {
        List<ProPlan> plans = planMapper.selectList(Wrappers.<ProPlan>lambdaQuery()
                .eq(ProPlan::getIsDel, G.ISDEL_NO)
                .eq(ProPlan::getPlan_type, planType));

        plans.forEach(plan  -> {
            plan.setPlan_status(3);  // 默认状态置为进行中
            List<?> children = getChildrenByType(plan, planType);

            if (CollUtil.isEmpty(children))  {
                planMapper.updateById(plan);
                return;
            }

            statusUpdater.accept(plan,  children);

            planMapper.updateById(plan);
        });
    }

    /**
     * 获取不同类型的子项集合
     */
    private List<?> getChildrenByType(ProPlan plan, int planType) {
        return planType == 3 ?
                taskPlanMapper.selectList(Wrappers.<TaskPlan>lambdaQuery()
                    .eq(TaskPlan::getIsDel, G.ISDEL_NO)
                    .eq(TaskPlan::getPlan_task_id, plan.getId()))  :
                planMapper.selectList(Wrappers.<ProPlan>lambdaQuery()
                    .eq(ProPlan::getIsDel, G.ISDEL_NO)
                    .eq(ProPlan::getParent_id, plan.getId()));
    }

    /**
     * 任务驱动型计划状态更新（原类型3逻辑）
     */
    private void updateTaskDrivenPlanStatus(ProPlan plan, List<?> children) {
        // 获取任务ID集合时直接转型
        Set<Integer> taskIds = ((List<TaskPlan>) children).stream()
                .map(TaskPlan::getTask_id)
                .collect(Collectors.toSet());

        // 联表查询优化：直接查询有效任务的状态
        boolean allComplete = taskInfoMapper.selectList(Wrappers.<TaskInfo>lambdaQuery()
                    .in(TaskInfo::getId, taskIds)
                    .eq(TaskInfo::getIsDel, G.ISDEL_NO))
                .stream()
                .allMatch(t -> "4".equals(t.getStatus()));  // 使用allMatch替代count统计

        plan.setPlan_status(allComplete  ? 3 : 2);
    }

    /**
     * 层级驱动型计划状态更新（原类型2逻辑）
     */
    private void updateHierarchyDrivenPlanStatus(ProPlan plan, List<?> children) {
        boolean allComplete = ((List<ProPlan>) children).stream()
                .allMatch(t -> t.getPlan_status()  == 3); // 直接判断所有子任务是否完成
        plan.setPlan_status(allComplete  ? 3 : 2);
    }






//    @Scheduled(cron = "0 0/5 * * * ?")
//    public void projectTask1() {
//        // 找到计划任务里面的所有任务id
//        List<ProPlan> planTasks = planMapper.selectList(Wrappers.<ProPlan>lambdaQuery()
//                .eq(ProPlan::getIsDel, G.ISDEL_NO).eq(ProPlan::getPlan_type, 3)
//        );
//        for (ProPlan planTask : planTasks) {
//            planTask.setPlan_status(1);
//            List<TaskPlan> taskPlans = taskPlanMapper.selectList(Wrappers.<TaskPlan>lambdaQuery().eq(TaskPlan::getIsDel, G.ISDEL_NO).eq(TaskPlan::getPlan_task_id, planTask.getId()));
//            if (CollUtil.isEmpty(taskPlans)){
//                planMapper.updateById(planTask);
//                continue;
//            }
//            Set<Integer> taskIds = taskPlans.stream().map(TaskPlan::getTask_id).collect(Collectors.toSet());
//            List<TaskInfo> taskInfos = taskInfoMapper.selectList(Wrappers.<TaskInfo>lambdaQuery().in(TaskInfo::getId, taskIds).eq(TaskInfo::getIsDel, G.ISDEL_NO));
//            // 使用Java8 Stream优化统计逻辑
//            long completeCount = taskInfos.stream().filter(t -> "4".equals(t.getStatus())).count();
//            boolean allComplete = taskInfos.size()  == completeCount;
//
//            if (allComplete){
//                planTask.setPlan_status(3);
//            }else {
//                planTask.setPlan_status(2);
//            }
//            planMapper.updateById(planTask);
//        }
//
//        // 根据计划任务的type,判断计划状态
//        List<ProPlan> plans = planMapper.selectList(Wrappers.<ProPlan>lambdaQuery()
//                .eq(ProPlan::getIsDel, G.ISDEL_NO).eq(ProPlan::getPlan_type, 2)
//        );
//        for(ProPlan plan : plans){
//            plan.setPlan_status(1);
//            List<ProPlan> children = planMapper.selectList(Wrappers.<ProPlan>lambdaQuery().eq(ProPlan::getIsDel, G.ISDEL_NO).eq(ProPlan::getParent_id, plan.getId()));
//            if (CollUtil.isEmpty(children)){
//                planMapper.updateById(plan);
//                continue;
//            }
//
//            long completeCount = children.stream().filter(t -> 3 == t.getPlan_status()).count();
//            boolean allComplete = children.size()  == completeCount;
//            if (allComplete){
//                plan.setPlan_status(3);
//            }else {
//                plan.setPlan_status(2);
//            }
//            planMapper.updateById(plan);
//        }
//
//    }


}
