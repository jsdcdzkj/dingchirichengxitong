package com.jsdc.worktime.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jsdc.worktime.config.formula.FormulaConfig;
import com.jsdc.worktime.config.formula.FormulaParams;
import com.jsdc.worktime.config.formula.FormulaUtils;
import com.jsdc.worktime.global.G;
import com.jsdc.worktime.mapper.pro_.ProMilestoneMapper;
import com.jsdc.worktime.mapper.pro_.ProReviewMapper;
import com.jsdc.worktime.mapper.sys_.SysDictMapper;
import com.jsdc.worktime.mapper.sys_.SysGroupMapper;
import com.jsdc.worktime.mapper.sys_.SysGroupUserMapper;
import com.jsdc.worktime.mapper.sys_.SysUserMapper;
import com.jsdc.worktime.mapper.task_.*;
import com.jsdc.worktime.mapper.test_.TestBugMapper;
import com.jsdc.worktime.pro_.ProMilestone;
import com.jsdc.worktime.pro_.ProReview;
import com.jsdc.worktime.service.task_.TaskStopService;
import com.jsdc.worktime.sys_.SysDict;
import com.jsdc.worktime.sys_.SysGroup;
import com.jsdc.worktime.sys_.SysGroupUser;
import com.jsdc.worktime.sys_.SysUser;
import com.jsdc.worktime.task_.*;
import com.jsdc.worktime.test_.TestBug;
import com.jsdc.worktime.utils.RedisUtil;
import com.jsdc.worktime.utils.TimeUtils;
import com.jsdc.worktime.vo.StatisticsUserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 任务工时
 * 定时任务
 */
@Component
@Slf4j
public class DeptTask {

    @Autowired
    private SysGroupMapper sysGroupMapper;
    @Autowired
    private SysDictMapper sysDictMapper;
    @Autowired
    private SysGroupUserMapper sysGroupUserMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private TaskInfoMapper taskInfoMapper;
    @Autowired
    private FormulaConfig formulaConfig;
    @Autowired
    private FormulaUtils formulaUtils;
    @Autowired
    private TaskPlanMapper taskPlanMapper;
    @Autowired
    private TaskStopService taskStopService;
    @Autowired
    private TestBugMapper testBugMapper;
    @Autowired
    private TaskOperationInfoMapper taskOperationInfoMapper;
    @Autowired
    private TaskLeadersInfoMapper taskLeadersInfoMapper;
    @Autowired
    private ProMilestoneMapper proMilestoneMapper;
    @Autowired
    private TaskLeadersCallBackMapper taskLeadersCallBackMapper;
    @Autowired
    private ProReviewMapper proReviewMapper;

    /**
     * 5分钟执行一次
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void taskInMouth() {
//        type 1.前端, 2.后端, 3.产品/项目经理, 4.测试
        List<Integer> types = Arrays.asList(1, 2, 3, 4);
        for (Integer type : types) {
            // 1. 得到所有组
            List<SysGroup> groups = sysGroupMapper.selectList(Wrappers.<SysGroup>lambdaQuery().eq(SysGroup::getIs_del, 0).eq(SysGroup::getType, getDictValue(type + "")));
            // 2. 得到组员
            List<SysGroupUser> groupUsers = sysGroupUserMapper.selectList(Wrappers.<SysGroupUser>lambdaQuery().in(SysGroupUser::getGroupId, groups.stream().map(SysGroup::getId).collect(Collectors.toList())));
            // 3. 得到组员名称
            List<SysUser> users = sysUserMapper.selectList(Wrappers.<SysUser>lambdaQuery()
                    .eq(SysUser::getIs_del, G.ISDEL_NO).in(SysUser::getId, groupUsers.stream().map(SysGroupUser::getUserId).collect(Collectors.toList()))
            );
            Map<String, String> userMap = new HashMap<>();

            // 按照月份放入redis
            getMonthList().forEach(map -> {
                List<FormulaParams> params = new ArrayList<>();
                for (SysUser user : users) {
                    userMap.put(user.getId() + "", user.getRealName());
                    // 计算能力值
                    params.add(getAbilityValue(user, MapUtil.getStr(map, "startDay"), MapUtil.getStr(map, "endDay")));
                    StatisticsUserVo a = getAbilityValueByTypeTo(user.getId() + "", MapUtil.getStr(map, "startDay"), MapUtil.getStr(map, "endDay"), null);
                    RedisUtil.set("deptStatic::getAbilityValueByTypeTo_" + MapUtil.getStr(map, "startDay") + "_" + MapUtil.getStr(map, "endDay") + "_" + user.getId(), JSONUtil.toJsonStr(a));
                }
                List<FormulaParams> result = new ArrayList<>();
                if (type == 1 || type == 2) {
                    result = formulaUtils.evaluatePersonalAbility(params);
                } else {
                    result = formulaUtils.evaluatePersonalAbility_manage(params);
                }
                for (FormulaParams formulaParams : result) {
                    formulaParams.setName(MapUtil.getStr(userMap, formulaParams.getUserId(), "暂无"));
                }
                RedisUtil.set("deptStatic::getDeptUserAnalysis_" + MapUtil.getStr(map, "startDay") + "_" + MapUtil.getStr(map, "endDay") + "_" + type, JSONUtil.toJsonStr(result));
            });
            // 按照季度放入redis
            getQuarterList().forEach(map -> {
                String startDay = MapUtil.getStr(map, "startDate");
                String endDay = MapUtil.getStr(map, "endDate");
                List<FormulaParams> params = new ArrayList<>();
                for (SysUser user : users) {
                    userMap.put(user.getId() + "", user.getRealName());
                    // 计算能力值
                    params.add(getAbilityValue(user, startDay, endDay));
                    StatisticsUserVo a = getAbilityValueByTypeTo(user.getId() + "", startDay, endDay, null);
                    RedisUtil.set("deptStatic::getAbilityValueByTypeTo_" + startDay + "_" + endDay + "_" + user.getId(), JSONUtil.toJsonStr(a));
                }
                List<FormulaParams> result = new ArrayList<>();
                if (type == 1 || type == 2) {
                    result = formulaUtils.evaluatePersonalAbility(params);
                } else {
                    result = formulaUtils.evaluatePersonalAbility_manage(params);
                }
                for (FormulaParams formulaParams : result) {
                    formulaParams.setName(MapUtil.getStr(userMap, formulaParams.getUserId(), "暂无"));
                }
                RedisUtil.set("deptStatic::getDeptUserAnalysis_" + startDay + "_" + endDay + "_" + type, JSONUtil.toJsonStr(result));
            });


        }
    }

    /**
     * 5分钟执行一次
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void staticAll() {
//        type 1.前端, 2.后端, 3.产品/项目经理, 4.测试
        List<Integer> types = Arrays.asList(1, 2, 3, 4);
        for (Integer type : types) {
            // 1. 得到所有组
            List<SysGroup> groups = sysGroupMapper.selectList(Wrappers.<SysGroup>lambdaQuery().eq(SysGroup::getIs_del, 0).eq(SysGroup::getType, getDictValue(type + "")));
            // 2. 得到组员
            List<SysGroupUser> groupUsers = sysGroupUserMapper.selectList(Wrappers.<SysGroupUser>lambdaQuery().in(SysGroupUser::getGroupId, groups.stream().map(SysGroup::getId).collect(Collectors.toList())));
            // 3. 得到组员名称
            List<SysUser> users = sysUserMapper.selectList(Wrappers.<SysUser>lambdaQuery()
                    .eq(SysUser::getIs_del, G.ISDEL_NO).in(SysUser::getId, groupUsers.stream().map(SysGroupUser::getUserId).collect(Collectors.toList()))
            );
            Map<String, String> userMap = new HashMap<>();

            List<FormulaParams> params = new ArrayList<>();
            for (SysUser user : users) {
                userMap.put(user.getId() + "", user.getRealName());
                // 计算能力值
                params.add(getAbilityValue(user, null, null));
                StatisticsUserVo a = getAbilityValueByTypeTo(user.getId() + "", null, null, null);
                RedisUtil.set("deptStatic::getAbilityValueByTypeTo_ALL_" + user.getId(), JSONUtil.toJsonStr(a));
            }

            List<FormulaParams> result = new ArrayList<>();
            if (type == 1 || type == 2) {
                result = formulaUtils.evaluatePersonalAbility(params);
            } else {
                result = formulaUtils.evaluatePersonalAbility_manage(params);
            }
            for (FormulaParams formulaParams : result) {
                formulaParams.setName(MapUtil.getStr(userMap, formulaParams.getUserId(), "暂无"));
            }
            RedisUtil.set("deptStatic::getDeptUserAnalysis_ALL_" + type, JSONUtil.toJsonStr(result));
        }
    }

    // 得到当前年份的每个月
    public List<Map<String, Object>> getMonthList() {
        List<Map<String, Object>> list = new ArrayList<>();
        int currentYear = LocalDate.now().getYear();  // 2025
        for (int month = 1; month <= 12; month++) {
            Map<String, Object> map = new HashMap<>();
            YearMonth yearMonth = YearMonth.of(currentYear, month);

            // 月份名称（如 "January"）
            String monthName = yearMonth.getMonth().toString();
            // 月份数字（如 1）
            int monthValue = yearMonth.getMonthValue();
            // 起始日期（当月第一天）
            String startDay = yearMonth.atDay(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
            // 结束日期（当月最后一天）
            String endDay = yearMonth.atEndOfMonth().format(DateTimeFormatter.ISO_LOCAL_DATE);
            // 当月总天数
            int daysInMonth = yearMonth.lengthOfMonth();

            map.put("monthName", monthName);
            map.put("monthValue", monthValue);
            map.put("startDay", startDay);
            map.put("endDay", endDay);
            map.put("daysInMonth", daysInMonth);

            list.add(map);
        }
        return list;
    }

    public List<Map<String, Object>> getQuarterList() {
        List<Map<String, Object>> quarters = new ArrayList<>();
        int currentYear = LocalDate.now().getYear();  // 2025

        for (int q = 1; q <= 4; q++) {
            Map<String, Object> quarterMap = new HashMap<>();
            // 计算季度起始月份和结束月份
            Month startMonth = Month.of((q - 1) * 3 + 1); // 1,4,7,10月
            Month endMonth = startMonth.plus(2);           // 3,6,9,12月

            // 季度起止日期
            LocalDate startDate = LocalDate.of(currentYear, startMonth, 1);
            LocalDate endDate = YearMonth.of(currentYear, endMonth).atEndOfMonth();

            // 季度总天数
            long daysInQuarter = endDate.toEpochDay() - startDate.toEpochDay() + 1;

            // 填充数据
            quarterMap.put("quarter", q);
            quarterMap.put("quarterName", "Q" + q);
            quarterMap.put("startDate", startDate.format(DateTimeFormatter.ISO_DATE));
            quarterMap.put("endDate", endDate.format(DateTimeFormatter.ISO_DATE));
            quarterMap.put("daysInQuarter", daysInQuarter);
//            quarterMap.put("months", List.of(startMonth.name(), startMonth.plus(1).name(), endMonth.name()));
            quarterMap.put("months", Arrays.asList(
                    startMonth.name(),
                    startMonth.plus(1).name(),
                    endMonth.name()
            ));

            quarters.add(quarterMap);
        }
        return quarters;
    }


    public String getDictValue(String type) {
        // 使用静态常量维护type与字典标签的映射关系
        final Map<String, String> TYPE_LABEL_MAP = new HashMap<String, String>() {{
            put("1", "前端工作组");
            put("2", "后端工作组");
            put("3", "产品工作组");
            put("4", "测试工作组");
        }};

        // 参数校验前置
        if (!TYPE_LABEL_MAP.containsKey(type)) {
            return null;
        }

        // 统一查询逻辑
        List<SysDict> dictList = sysDictMapper.selectList(
                Wrappers.<SysDict>lambdaQuery()
                        .eq(SysDict::getDictSign, "sysGroup")
                        .eq(SysDict::getIsDel, G.ISDEL_NO)
                        .eq(SysDict::getDictLabel, TYPE_LABEL_MAP.get(type))
        );

        // 使用Optional处理空值
        return Optional.ofNullable(dictList)
                .filter(CollUtil::isNotEmpty)
                .map(list -> list.get(0).getDictValue())
                .orElse(null);
    }


    // 得到能力值
    public FormulaParams getAbilityValue(SysUser user, String startDate, String endDate) {
        FormulaParams params = new FormulaParams();

        // 需要的参数, userId
        params.setUserId(user.getId() + "");

        // 时间条件预处理
        final boolean hasValidDate = StrUtil.isAllNotBlank(startDate, endDate);
        final String startTime = hasValidDate ? startDate + " 00:00:00" : null;
        final String endTime = hasValidDate ? endDate + " 23:59:59" : null;

        // TODO 效率 efficiency
        // 任务总数(计划任务+运维任务+领导交办任务+bug任务)
        Number taskCount = 0;
        // （Σ任务计划⼯时/实际⼯时）叠加
        Number taskSum = 0;
        // TODO  复杂度 complexity
        // 功能点*难度
        Number complexityNum = 0;
        // 功能点数量
        Number complexityCount = 0;
        // （Σ任务计划⼯时/实际⼯时）/任务数
        // 计划任务
        List<TaskInfo> taskInfos = taskInfoMapper.selectList(Wrappers.<TaskInfo>lambdaQuery()
                .eq(TaskInfo::getAssigned_to_user_id, user.getId())
                .between(hasValidDate, TaskInfo::getCreateTime, startTime, endTime)
                .eq(TaskInfo::getStatus, "4")
                .eq(TaskInfo::getIsDel, G.ISDEL_NO)
        );

        for (TaskInfo taskInfo : taskInfos) {
            //计划任务难易度
            BigDecimal num2 = null;
            switch (taskInfo.getDegree_of_difficulty()) {
                case "1":
                    num2 = new BigDecimal(formulaConfig.getDifficulty().get("easy"));
                    break;
                case "2":
                    num2 = new BigDecimal(formulaConfig.getDifficulty().get("simple"));
                    break;
                case "3":
                    num2 = new BigDecimal(formulaConfig.getDifficulty().get("normal"));
                    break;
                case "4":
                    num2 = new BigDecimal(formulaConfig.getDifficulty().get("hard"));
                    break;
                case "5":
                    num2 = new BigDecimal(formulaConfig.getDifficulty().get("challenge"));
                    break;
            }
            // 得到这个任务绑定了多少个功能点,如果没有绑定功能点,则默认为1
            int count = taskPlanMapper.selectCount(Wrappers.<TaskPlan>lambdaQuery().eq(TaskPlan::getTask_id, taskInfo.getId()));
            if (count == 0) {
                count = 1;
            }
            if (num2 != null) {
                complexityNum = NumberUtil.add(complexityNum, num2.multiply(new BigDecimal(count)));
            }
            complexityCount = NumberUtil.add(complexityCount, new BigDecimal(count));

            // 计算工作效率
            if (null == taskInfo.getPlaning_hours() || 0 == taskInfo.getPlaning_hours()) {
                continue;
            }
            // 得到实际工时
            String b = taskStopService.getHoursByTaskId(taskInfo.getId(), "1");
            if (StrUtil.isBlank(b) || "0".equals(b)) {
                continue;
            }
            // 排除项 6/2=3 排除
            Number num = safeDivide(NumberUtil.parseNumber(taskInfo.getPlaning_hours() + ""), NumberUtil.parseNumber(b), 2, new BigDecimal(0));
            // 标准值
            Number bzNum = NumberUtil.parseNumber(formulaConfig.getEfficiency());
            if (NumberUtil.compare(num.doubleValue(), bzNum.doubleValue()) > 0) {
                continue;
            }

            taskCount = NumberUtil.add(taskCount, 1);
            taskSum = NumberUtil.add(taskSum, num);


        }
        // 运维任务
        List<TaskOperationInfo> taskOperationInfos = taskOperationInfoMapper.selectList(Wrappers.<TaskOperationInfo>lambdaQuery()
                .eq(TaskOperationInfo::getAssigned_to_user_id, user.getId())
                .between(StrUtil.isNotBlank(startDate) && StrUtil.isNotBlank(endDate), TaskOperationInfo::getCreateTime, startDate + " 00:00:00", endDate + " 23:59:59")
                .eq(TaskOperationInfo::getStatus, "4")
                .eq(TaskOperationInfo::getIsDel, G.ISDEL_NO)
        );
        for (TaskOperationInfo taskInfo : taskOperationInfos) {


            //计划任务难易度
            BigDecimal num2 = null;
            switch (taskInfo.getDegree_of_difficulty()) {
                case "1":
                    num2 = new BigDecimal(formulaConfig.getDifficulty().get("easy"));
                    break;
                case "2":
                    num2 = new BigDecimal(formulaConfig.getDifficulty().get("simple"));
                    break;
                case "3":
                    num2 = new BigDecimal(formulaConfig.getDifficulty().get("normal"));
                    break;
                case "4":
                    num2 = new BigDecimal(formulaConfig.getDifficulty().get("hard"));
                    break;
                case "5":
                    num2 = new BigDecimal(formulaConfig.getDifficulty().get("challenge"));
                    break;
            }
            // 得到这个任务绑定了多少个功能点,如果没有绑定功能点,则默认为1
            int count = 1;
            if (num2 != null) {
                complexityNum = NumberUtil.add(complexityNum, num2.multiply(new BigDecimal(count)));
            }
            complexityCount = NumberUtil.add(complexityCount, new BigDecimal(count));

            // 计算工作效率
            if (null == taskInfo.getPlan_hours() || 0 == taskInfo.getPlan_hours()) {
                continue;
            }
            // 得到实际工时 @Comment("类型 1计划任务 2bug任务 3运维任务 4领导交办任务")
            String b = taskStopService.getHoursByTaskId(taskInfo.getId(), "3");
            if (StrUtil.isBlank(b) || "0".equals(b)) {
                continue;
            }
            // 排除项 6/2=3 排除
            Number num = NumberUtil.div(taskInfo.getPlan_hours() + "", b).setScale(2, RoundingMode.HALF_UP);
            // 标准值
            Number bzNum = NumberUtil.parseNumber(formulaConfig.getEfficiency());
            if (NumberUtil.compare(num.doubleValue(), bzNum.doubleValue()) > 0) {
                continue;
            }

            taskCount = NumberUtil.add(taskCount, 1);
            taskSum = NumberUtil.add(taskSum, num);

        }
        Number efficiency = safeDivide(taskSum, taskCount, 4, new BigDecimal(0));
        params.setEfficiency(NumberUtil.mul(efficiency.toString(), "1").setScale(2, RoundingMode.HALF_UP).toString());
        Number complexity = safeDivide(complexityNum, complexityCount, 4, new BigDecimal(0));
        params.setComplexity(NumberUtil.mul(complexity.toString(), "1").setScale(2, RoundingMode.HALF_UP).toString());

        // bug率 bugRate
        Number bugNum = 0;
        Number bugFunDif = 0;
        // bug数/（Σ功能点*难度）
        Integer bugCount = testBugMapper.selectCount(Wrappers.<TestBug>lambdaQuery().eq(TestBug::getIsDel, G.ISDEL_NO)
                .between(StrUtil.isNotBlank(startDate) && StrUtil.isNotBlank(endDate), TestBug::getCreateTime, startDate + " 00:00:00", endDate + " 23:59:59")
                .eq(TestBug::getAssigned_to_user_id, user.getId())
                .ne(TestBug::getStates, "3")
        );
        BigDecimal result2 = new BigDecimal(0);
        if (0 != bugCount) {
            result2 = safeDivide(bugCount, complexityNum, 2, new BigDecimal(0));

            if (result2.compareTo(new BigDecimal(formulaConfig.getBugFreeRate())) > 0) {
                result2 = new BigDecimal(formulaConfig.getBugFreeRate());
            }
        }

        params.setBugRate(result2.toString());


        return params;
    }

    /**
     * 安全除法计算（支持所有Number子类）
     *
     * @param dividend     被除数
     * @param divisor      除数
     * @param scale        精度位数（默认2位）
     * @param defaultValue 异常返回值（默认0）
     */
    public static BigDecimal safeDivide(Number dividend, Number divisor,
                                        int scale, BigDecimal defaultValue) {
        // 空值防御（支持Optional包装）
        BigDecimal a = Optional.ofNullable(dividend)
                .map(n -> new BigDecimal(n.toString()))
                .orElse(BigDecimal.ZERO);

        BigDecimal b = Optional.ofNullable(divisor)
                .map(n -> new BigDecimal(n.toString()))
                .orElse(BigDecimal.ZERO);

        // 零值/无效值判断
        if (b.compareTo(BigDecimal.ZERO) == 0 ||
                a.compareTo(BigDecimal.ZERO) == 0) {
            return defaultValue.setScale(scale, RoundingMode.HALF_UP);
        }

        try {
            return a.divide(b, scale, RoundingMode.HALF_UP);
        } catch (ArithmeticException e) {
            // 处理无限小数场景（如1/3）
            return a.divide(b, new MathContext(scale, RoundingMode.HALF_UP));
        }
    }

    // 后端,前端 统计
    public StatisticsUserVo getAbilityValueByTypeTo(String userId, String startDate, String endDate, String type) {
        StatisticsUserVo result = new StatisticsUserVo();
        // 预估工作时长-选定时间内的所有任务的预估工作时长
        Number planHours = 0;
        // 任务工作时长-选定时间内的所有任务的实际工作时长
        Number taskHours = 0;
        // BUG修复工作时长-选定时间内的所有BUG修复任务的实际工作时长
        Number bugHours = 0;
        // 挑战任务数
        Number challengeCount = 0;
        // 困难任务数
        Number difficultCount = 0;
        // 正常任务数
        Number normalCount = 0;
        // 简单任务数
        Number easyCount = 0;
        // 轻松任务数
        Number lightCount = 0;
        // 挑战功能数
        Number challengeFun = 0;
        // 困难功能数
        Number difficultFun = 0;
        // 正常及以下功能数
        Number normalFun = 0;

        // BUG率占比-选定时间内,个人BUG总数/个人任务功能点总数
        Number bugRate = 0;
        // 挑战功能bug率
        Number challengeBugRate = 0;
        // 困难功能bug率
        Number difficultBugRate = 0;
        // 正常及以下功能bug率
        Number normalBugRate = 0;

        // 总功能点数
        Number funCount = 0;
        // 总bug数
        Number bugCount = 0;
        // 挑战bug数
        Number challengeBug = 0;
        // 困难bug数
        Number difficultBug = 0;
        // 正常及以下bug数
        Number normalBug = 0;

        // 工作时限分析
        // 提前完成任务数量
        Number earlyCount = 0;
        // 准时完成任务数量
        Number onTimeCount = 0;
        // 延期完成任务数量
        Number delayCount = 0;

        // 内部评审总得分
        Number reviewScore = 0;
        // 评审总得分
        Number score = 0;
        // 验收一次通过率
        Number passRate = 0;

        // 领导打回数
        Number backCount = 0;
        // 领导交办任务数
        Number taskCount = 0;
        // 领导交办得分
        Number taskScore = 0;
        // 领导交办得分的个数
        Number taskScoreCount = 0;

        // 里程碑完成率
        Number milestoneRate = 0;
        // 已完成的里程碑数
        Number milestoneCount = 0;
        // 已完成的里程碑数量并且是正常通过的数量
        Number milestonePassCount = 0;
        // 里程碑总数
        Number milestoneTotal = 0;
        // 里程碑正常通过数量
        Number milestonePass = 0;

        // 测试的任务数量
        Number testTaskCount = 0;
        // 按期完成任务的数量
        Number onTimeTaskCount = 0;
        // test 中的任务bug
        Number testBugByTask = 0;


        // 记录的任务id
        Set<Integer> taskIds = new HashSet<>();
        // 记录项目id
        Set<Integer> projectIds = new HashSet<>();
        // 得到任务表
        List<TaskInfo> taskInfos = taskInfoMapper.selectList(Wrappers.<TaskInfo>lambdaQuery()
                .eq(TaskInfo::getAssigned_to_user_id, userId)
                .between(StrUtil.isNotBlank(startDate) && StrUtil.isNotBlank(endDate), TaskInfo::getCreateTime, startDate + " 00:00:00", endDate + " 23:59:59")
                .eq(TaskInfo::getIsDel, G.ISDEL_NO)
        );
        for (TaskInfo taskInfo : taskInfos) {
            planHours = NumberUtil.add(planHours, taskInfo.getPlaning_hours());
            // 实际工时
            String hours = taskStopService.getHoursByTaskId(taskInfo.getId(), "1");
            taskHours = NumberUtil.add(taskHours.toString(), hours);
            // 任务表id
            taskIds.add(taskInfo.getId());
            // 项目表id
            projectIds.add(taskInfo.getProject_id());
            // 完成效率
            if (StrUtil.isNotBlank(taskInfo.getDelay_status())) {
                switch (taskInfo.getDelay_status()) {
                    case "1":
                        earlyCount = NumberUtil.add(earlyCount, 1);
                        break;
                    case "2":
                        onTimeCount = NumberUtil.add(onTimeCount, 1);
                        break;
                    case "3":
                        delayCount = NumberUtil.add(delayCount, 1);
                        break;
                }
            }

            // 得到绑定的功能点数量
            int count = taskPlanMapper.selectCount(Wrappers.<TaskPlan>lambdaQuery().eq(TaskPlan::getTask_id, taskInfo.getId()));
            funCount = NumberUtil.add(funCount, count);

            // 统计任务难易度
            if (StrUtil.isNotBlank(taskInfo.getDegree_of_difficulty())) {
                switch (taskInfo.getDegree_of_difficulty()) {
                    case "1":
                        lightCount = NumberUtil.add(lightCount, 1);
                        normalFun = NumberUtil.add(normalFun, count);
                        break;
                    case "2":
                        easyCount = NumberUtil.add(easyCount, 1);
                        normalFun = NumberUtil.add(normalFun, count);
                        break;
                    case "3":
                        normalCount = NumberUtil.add(normalCount, 1);
                        normalFun = NumberUtil.add(normalFun, count);
                        break;
                    case "4":
                        difficultCount = NumberUtil.add(difficultCount, 1);
                        difficultFun = NumberUtil.add(difficultFun, count);
                        break;
                    case "5":
                        challengeCount = NumberUtil.add(challengeCount, 1);
                        challengeFun = NumberUtil.add(challengeFun, count);
                        break;
                }
            }

            // 测试任务
            if (StrUtil.equals("4", taskInfo.getTask_attribute())) {
                testTaskCount = NumberUtil.add(testTaskCount, 1);
                // 完成效率
                if (StrUtil.isNotBlank(taskInfo.getDelay_status())) {
                    switch (taskInfo.getDelay_status()) {
                        case "1":
                            onTimeTaskCount = NumberUtil.add(onTimeTaskCount, 1);
                            break;
                        case "2":
                            onTimeTaskCount = NumberUtil.add(onTimeTaskCount, 1);
                            break;
                    }
                }
            }
        }

        // 得到运维任务表
        List<TaskOperationInfo> taskOperationInfos = taskOperationInfoMapper.selectList(Wrappers.<TaskOperationInfo>lambdaQuery()
                .eq(TaskOperationInfo::getAssigned_to_user_id, userId)
                .between(StrUtil.isNotBlank(startDate) && StrUtil.isNotBlank(endDate), TaskOperationInfo::getCreateTime, startDate + " 00:00:00", endDate + " 23:59:59")
                .eq(TaskOperationInfo::getIsDel, G.ISDEL_NO)
        );
        for (TaskOperationInfo taskOperationInfo : taskOperationInfos) {
            planHours = NumberUtil.add(planHours, taskOperationInfo.getPlan_hours());
            // 实际工时
            String hours = taskStopService.getHoursByTaskId(taskOperationInfo.getId(), "3");
            taskHours = NumberUtil.add(taskHours.toString(), hours);

            // 完成效率
            if (StrUtil.isNotBlank(taskOperationInfo.getDelay_status())) {
                switch (taskOperationInfo.getDelay_status()) {
                    case "1":
                        earlyCount = NumberUtil.add(earlyCount, 1);
                        break;
                    case "2":
                        onTimeCount = NumberUtil.add(onTimeCount, 1);
                        break;
                    case "3":
                        delayCount = NumberUtil.add(delayCount, 1);
                        break;
                }
            }

            // 统计任务难易度
            if (StrUtil.isNotBlank(taskOperationInfo.getDegree_of_difficulty())) {
                switch (taskOperationInfo.getDegree_of_difficulty()) {
                    case "1":
                        lightCount = NumberUtil.add(lightCount, 1);
                        break;
                    case "2":
                        easyCount = NumberUtil.add(easyCount, 1);
                        break;
                    case "3":
                        normalCount = NumberUtil.add(normalCount, 1);
                        break;
                    case "4":
                        difficultCount = NumberUtil.add(difficultCount, 1);
                        break;
                    case "5":
                        challengeCount = NumberUtil.add(challengeCount, 1);
                        break;
                }
            }
        }

        // 得到领导交办任务表
        List<TaskLeadersInfo> taskLeadersInfos = taskLeadersInfoMapper.selectList(Wrappers.<TaskLeadersInfo>lambdaQuery()
                .eq(TaskLeadersInfo::getAssigned_to_user_id, userId)
                .between(StrUtil.isNotBlank(startDate) && StrUtil.isNotBlank(endDate), TaskLeadersInfo::getCreateTime, startDate + " 00:00:00", endDate + " 23:59:59")
                .eq(TaskLeadersInfo::getIsDel, G.ISDEL_NO)
        );
        for (TaskLeadersInfo taskLeadersInfo : taskLeadersInfos) {
            planHours = NumberUtil.add(planHours, taskLeadersInfo.getPlan_hours());
            // 实际工时
            String hours = taskStopService.getHoursByTaskId(taskLeadersInfo.getId(), "4");
            taskHours = NumberUtil.add(taskHours.toString(), hours);

            // 完成效率
            if (StrUtil.isNotBlank(taskLeadersInfo.getDelay_status())) {
                switch (taskLeadersInfo.getDelay_status()) {
                    case "1":
                        earlyCount = NumberUtil.add(earlyCount, 1);
                        break;
                    case "2":
                        onTimeCount = NumberUtil.add(onTimeCount, 1);
                        break;
                    case "3":
                        delayCount = NumberUtil.add(delayCount, 1);
                        break;
                }
            }

            // 统计任务难易度
            if (StrUtil.isNotBlank(taskLeadersInfo.getDegree_of_difficulty())) {
                switch (taskLeadersInfo.getDegree_of_difficulty()) {
                    case "1":
                        lightCount = NumberUtil.add(lightCount, 1);
                        break;
                    case "2":
                        easyCount = NumberUtil.add(easyCount, 1);
                        break;
                    case "3":
                        normalCount = NumberUtil.add(normalCount, 1);
                        break;
                    case "4":
                        difficultCount = NumberUtil.add(difficultCount, 1);
                        break;
                    case "5":
                        challengeCount = NumberUtil.add(challengeCount, 1);
                        break;
                }

            }

            // 查询任务是否有打回记录
            int count = taskLeadersCallBackMapper.selectCount(Wrappers.<TaskLeadersCallBack>lambdaQuery()
                    .eq(TaskLeadersCallBack::getTask_leaders_id, taskLeadersInfo.getId())
            );
            if (count > 0) {
                backCount = NumberUtil.add(backCount, 1);
            }
            taskCount = NumberUtil.add(taskCount, 1);

            // 得分
            if (StrUtil.isNotBlank(taskLeadersInfo.getTask_rating())) {
                taskScore = NumberUtil.add(taskScore.toString(), taskLeadersInfo.getTask_rating());
                taskScoreCount = NumberUtil.add(taskScoreCount, 1);
            }
        }

        // 得到bug修复任务表
        List<TestBug> testBugs = testBugMapper.selectList(Wrappers.<TestBug>lambdaQuery()
                .eq(TestBug::getAssigned_to_user_id, userId)
                .between(StrUtil.isNotBlank(startDate) && StrUtil.isNotBlank(endDate), TestBug::getCreateTime, startDate + " 00:00:00", endDate + " 23:59:59")
                .ne(TestBug::getStates, "3")
                .eq(TestBug::getIsDel, G.ISDEL_NO)
        );
        for (TestBug testBug : testBugs) {
            // 实际工时
            String hours = taskStopService.getHoursByTaskId(testBug.getId(), "2");
            bugHours = NumberUtil.add(bugHours.toString(), hours);

            // bug数量
            bugCount = NumberUtil.add(bugCount, 1);
            if (1 == testBug.getSource()) {
                testBugByTask = NumberUtil.add(testBugByTask, 1);
            }

            // 根据功能点id查询任务难度
            if (null == testBug.getPro_feature_id() || CollUtil.isEmpty(taskIds)) {
                continue;
            }
            List<TaskPlan> planTasks = taskPlanMapper.selectList(Wrappers.<TaskPlan>lambdaQuery()
                    .eq(TaskPlan::getFeature_id, testBug.getPro_feature_id())
                    .in(TaskPlan::getTask_id, taskIds)
            );
            if (CollUtil.isEmpty(planTasks)) {
                continue;
            }
            Integer taskId = planTasks.get(0).getTask_id();
            TaskInfo taskInfo = taskInfoMapper.selectById(taskId);
            if (StrUtil.isNotBlank(taskInfo.getDegree_of_difficulty())) {
                switch (taskInfo.getDegree_of_difficulty()) {
                    case "1":
                        normalBug = NumberUtil.add(normalBug, 1);
                        break;
                    case "2":
                        normalBug = NumberUtil.add(normalBug, 1);
                        break;
                    case "3":
                        normalBug = NumberUtil.add(normalBug, 1);
                        break;
                    case "4":
                        difficultBug = NumberUtil.add(difficultBug, 1);
                        break;
                    case "5":
                        challengeBug = NumberUtil.add(challengeBug, 1);
                        break;
                }
            }
        }

        bugRate = safeDivide(bugCount, funCount, 4, new BigDecimal(0));
        //调整为个人能力值分析的bug率
        FormulaParams a = getAbilityValue(sysUserMapper.selectById(userId), startDate, endDate);
        bugRate = Optional.ofNullable(a.getBugRate())
                .map(BigDecimal::new)
                .orElse((BigDecimal) bugRate);

        challengeBugRate = safeDivide(challengeBug, challengeFun, 4, new BigDecimal(0));
        difficultBugRate = safeDivide(difficultBug, normalFun, 4, new BigDecimal(0));
        normalBugRate = safeDivide(normalBug, normalFun, 4, new BigDecimal(0));

        // 得到评审的分数
        if (CollUtil.isNotEmpty(projectIds)) {
            List<ProMilestone> milestones = proMilestoneMapper.selectList(Wrappers.<ProMilestone>lambdaQuery()
                    .eq(ProMilestone::getState, "1")
                    .between(StrUtil.isNotBlank(startDate) && StrUtil.isNotBlank(endDate), ProMilestone::getCreateTime, startDate + " 00:00:00", endDate + " 23:59:59")
                    .in(ProMilestone::getProjectId, projectIds)
                    .eq(ProMilestone::getIsDel, G.ISDEL_NO)
            );
            if (CollUtil.isNotEmpty(milestones)) {
                for (ProMilestone milestone : milestones) {
                    milestoneTotal = NumberUtil.add(milestoneTotal, 1);
                    if (milestone.getState().equals("1")) {
                        milestoneCount = NumberUtil.add(milestoneCount, 1);
                    }

                    //评审信息
                    List<ProReview> proReviews = proReviewMapper.selectList(new LambdaQueryWrapper<ProReview>()
                            .eq(ProReview::getIsDel, 0)
                            .eq(ProReview::getMilestoneId, milestone.getId()));
                    if (CollUtil.isNotEmpty(proReviews)) {
                        ProReview proReview = proReviews.get(0);
                        //评审分数
                        reviewScore = NumberUtil.add(reviewScore.toString(), proReview.getReviewTotalPoints());
                        //总分数
                        score = NumberUtil.add(score, 100);
                        // 正常通过数量
                        if (null != proReview.getPostponeStatus()) {
                            if (proReview.getPostponeStatus() == 1 || proReview.getPostponeStatus() == 2) {
                                milestonePass = NumberUtil.add(milestonePass, 1);
                            }
                        }
                    }
                }
            }
        }

        // 一次性通过率 选定时间内,需求阶段，内部评审总得分/内部评审总分值
        passRate = safeDivide(reviewScore, score, 4, new BigDecimal(0));
        // 领导交办任务打回率
        Number backRate = safeDivide(backCount, taskCount, 4, new BigDecimal(0));
        // 领导交办任务平均分
        Number taskScoreAvg = safeDivide(taskScore, taskScoreCount, 2, new BigDecimal(0));
        // 按实开展项目评审率
        milestoneRate = safeDivide(milestoneCount, milestoneTotal, 4, new BigDecimal(0));

        // 测试任务按期完成率
        Number onTimeTaskRate = safeDivide(onTimeTaskCount, testTaskCount, 4, new BigDecimal(0));
        // 测试报告bug覆盖率
        Number testBugRate = safeDivide(testBugByTask, testTaskCount, 4, new BigDecimal(0));

        // 评审通过率
//        Number milestonePassRate = safeDivide(milestonePass, milestoneTotal,4, new BigDecimal(0));
        Number milestonePassRate = safeDivide(milestonePass, milestoneCount, 4, new BigDecimal(0));

        // TODO 工作时长分析
        result.setPlanHours(TimeUtils.convertMinToDayHourMin(planHours.intValue()));
        result.setTaskHours(TimeUtils.convertMinToDayHourMin(taskHours.intValue()));
        result.setBugHours(TimeUtils.convertMinToDayHourMin(bugHours.intValue()));
        // TODO 工作内容分析
        result.setChallengeCount(challengeCount.intValue());
        result.setDifficultCount(difficultCount.intValue());
        result.setNormalCount(normalCount.intValue());
        result.setEasyCount(easyCount.intValue());
        result.setLightCount(lightCount.intValue());
        result.setChallengeFun(challengeFun.intValue());
        result.setDifficultFun(difficultFun.intValue());
        result.setNormalFun(normalFun.intValue());
        // TODO 工作质量分析 * 100%
        result.setBugRate(NumberUtil.mul(bugRate, 100).setScale(2, RoundingMode.HALF_UP).toString());
        result.setPassRate(NumberUtil.mul(passRate, 100).setScale(2, RoundingMode.HALF_UP).toString());
        result.setChallengeBugRate(NumberUtil.mul(challengeBugRate, 100).setScale(2, RoundingMode.HALF_UP).toString());
        result.setDifficultBugRate(NumberUtil.mul(difficultBugRate, 100).setScale(2, RoundingMode.HALF_UP).toString());
        result.setNormalBugRate(NumberUtil.mul(normalBugRate, 100).setScale(2, RoundingMode.HALF_UP).toString());
        result.setActiveProjectCount(projectIds.size());
        result.setCompleteFunCount(funCount.intValue());
        result.setLeadBackRate(NumberUtil.mul(backRate, 100).setScale(2, RoundingMode.HALF_UP).toString());
        result.setTaskScoreAvg(taskScoreAvg.toString());
        result.setTestTaskRate(NumberUtil.mul(onTimeTaskRate, 100).setScale(2, RoundingMode.HALF_UP).toString());
        result.setTestBugRate(NumberUtil.mul(testBugRate, 100).setScale(2, RoundingMode.HALF_UP).toString());
        result.setMilestonePassRate(NumberUtil.mul(milestonePassRate, 100).setScale(2, RoundingMode.HALF_UP).toString());

        // TODO 工作时限分析
        result.setEarlyCount(earlyCount.intValue());
        result.setOnTimeCount(onTimeCount.intValue());
        result.setDelayCount(delayCount.intValue());
        result.setProjectReviewRate(NumberUtil.mul(milestoneRate, 100).setScale(2, RoundingMode.HALF_UP).toString());

        return result;
    }


}
