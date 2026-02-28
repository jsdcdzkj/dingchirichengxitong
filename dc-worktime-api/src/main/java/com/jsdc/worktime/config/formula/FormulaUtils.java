package com.jsdc.worktime.config.formula;

import com.jsdc.worktime.service.sys_.SysUserService;
import com.jsdc.worktime.sys_.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class FormulaUtils {

    @Autowired
    private SysUserService sysUserService ;

    @Autowired
    private FormulaConfig formulaConfig;

    private static final MathContext MC = new MathContext(10, RoundingMode.HALF_UP);
    private static final int SCALE = 2;

    /**
     * 计算项目贡献值
     * @param formulaParamsList
     * @return
     */
    public List<FormulaParams> evaluateContribution(List<FormulaParams> formulaParamsList) {
        if (formulaParamsList == null || formulaParamsList.isEmpty())  {
            return Collections.emptyList();
        }

        // 1. 转换数据类型并收集最大值
        Map<String, BigDecimal> maxValues = new HashMap<>();
        Map<String, Map<String, BigDecimal>> userValues = new HashMap<>();

        // 首次遍历：转换数据类型并找到各指标最大值
        for (FormulaParams params : formulaParamsList) {
            try {
                BigDecimal workload = new BigDecimal(params.getWorkload());
                BigDecimal efficiency = new BigDecimal(params.getEfficiency());
                BigDecimal complexity = new BigDecimal(params.getComplexity());
                BigDecimal bugRate = new BigDecimal(params.getBugRate());

                // 更新各指标最大值
                maxValues.put("workload",  max(maxValues.get("workload"),  workload));
                maxValues.put("efficiency",  max(maxValues.get("efficiency"),  efficiency));
                maxValues.put("complexity",  max(maxValues.get("complexity"),  complexity));
                maxValues.put("bugRate",  max(maxValues.get("bugRate"),  bugRate));

                // 存储原始值（使用Java 8兼容的方式创建Map）
                Map<String, BigDecimal> valueMap = new HashMap<>();
                valueMap.put("workload",  workload);
                valueMap.put("efficiency",  efficiency);
                valueMap.put("complexity",  complexity);
                valueMap.put("bugRate",  bugRate);
                userValues.put(params.getUserId(),  valueMap);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format for user: " + params.getUserId());
            }
        }

        // 2. 计算加权总分
        Map<String, BigDecimal> weightedScores = new HashMap<>();
        Map<String, BigDecimal> weights = new HashMap<>();
        formulaConfig.getWeights().forEach((k,  v) -> weights.put(k,  new BigDecimal(v.toString())));

        // 计算每个用户的加权总分
        for (Map.Entry<String, Map<String, BigDecimal>> entry : userValues.entrySet())  {
            String userId = entry.getKey();
            Map<String, BigDecimal> values = entry.getValue();

            BigDecimal normalizedWorkload = safeDivide(values.get("workload"),  maxValues.get("workload"));
            BigDecimal normalizedEfficiency = safeDivide(values.get("efficiency"),  maxValues.get("efficiency"));
            BigDecimal normalizedComplexity = safeDivide(values.get("complexity"),  maxValues.get("complexity"));
            BigDecimal normalizedBugRate = safeDivide(values.get("bugRate"),  maxValues.get("bugRate"));

            BigDecimal weightedScore = normalizedWorkload.multiply(weights.get("workload"),  MC)
                    .add(normalizedEfficiency.multiply(weights.get("efficiency"),  MC), MC)
                    .add(normalizedComplexity.multiply(weights.get("difficulty"),  MC), MC)
                    .add(normalizedBugRate.multiply(weights.get("bugRate"),  MC), MC);

            weightedScores.put(userId,  weightedScore);
        }


        // 4. 生成最终结果
        List<FormulaParams> result = new ArrayList<>();
        for (FormulaParams params : formulaParamsList) {
            BigDecimal score = weightedScores.get(params.getUserId())
                    .multiply(new BigDecimal("100"), MC)
                    .setScale(SCALE, RoundingMode.HALF_UP);

            FormulaParams resultParams = new FormulaParams();
            SysUser sysUser = sysUserService.getById(params.getUserId()) ;
            resultParams.setName(sysUser.getRealName());
            resultParams.setUserId(params.getUserId());
            resultParams.setWorkload(params.getWorkload());
            resultParams.setEfficiency(params.getEfficiency());
            resultParams.setComplexity(params.getComplexity());
            resultParams.setBugRate(params.getBugRate());
            resultParams.setScore(score.toPlainString());

            result.add(resultParams);
        }

        return result;
    }


    public List<FormulaParams> evaluatePersonalAbility(List<FormulaParams> formulaParamsList){
        if (formulaParamsList == null || formulaParamsList.isEmpty())  {
            return Collections.emptyList();
        }

        // 1. 转换数据类型并收集最大值
        Map<String, BigDecimal> maxValues = new HashMap<>();
        Map<String, Map<String, BigDecimal>> userValues = new HashMap<>();

        // 首次遍历：转换数据类型并找到各指标最大值
        for (FormulaParams params : formulaParamsList) {
            try {
                BigDecimal efficiency = new BigDecimal(params.getEfficiency());
                BigDecimal complexity = new BigDecimal(params.getComplexity());
                BigDecimal bugRate = new BigDecimal(formulaConfig.getBugFreeRate());
                BigDecimal bugRateOne =BigDecimal.ONE.divide(bugRate,MC);
                if(bugRateOne.compareTo(new BigDecimal(params.getBugRate())) < 0){
                    bugRate = BigDecimal.ONE.divide(new BigDecimal(params.getBugRate()),MC);
                }

                // 更新各指标最大值
                maxValues.put("efficiency",  max(maxValues.get("efficiency"),  efficiency));
                maxValues.put("complexity",  max(maxValues.get("complexity"),  complexity));
                maxValues.put("bugRate",  max(maxValues.get("bugRate"),  bugRate));

                // 存储原始值（使用Java 8兼容的方式创建Map）
                Map<String, BigDecimal> valueMap = new HashMap<>();
                valueMap.put("efficiency",  efficiency);
                valueMap.put("complexity",  complexity);
                valueMap.put("bugRate",  bugRate);
                userValues.put(params.getUserId(),  valueMap);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format for user: " + params.getUserId());
            }
        }

        // 2. 计算加权总分
        Map<String, BigDecimal> weightedScores = new HashMap<>();
        Map<String, BigDecimal> weights = new HashMap<>();
        formulaConfig.getDevWeights().forEach((k,  v) -> weights.put(k,  new BigDecimal(v.toString())));

        // 计算每个用户的加权总分
        for (Map.Entry<String, Map<String, BigDecimal>> entry : userValues.entrySet())  {
            String userId = entry.getKey();
            Map<String, BigDecimal> values = entry.getValue();

            BigDecimal normalizedEfficiency = safeDivide(values.get("efficiency"),  maxValues.get("efficiency"));
            BigDecimal normalizedComplexity = safeDivide(values.get("complexity"),  maxValues.get("complexity"));
            BigDecimal normalizedBugRate = safeDivide(values.get("bugRate"),  maxValues.get("bugRate"));

            BigDecimal weightedScore = normalizedEfficiency.multiply(weights.get("efficiency"),  MC)
                    .add(normalizedComplexity.multiply(weights.get("difficulty"),  MC), MC)
                    .add(normalizedBugRate.multiply(weights.get("bugRate"),  MC), MC);

            weightedScores.put(userId,  weightedScore);
        }


        // 4. 生成最终结果
        List<FormulaParams> result = new ArrayList<>();
        for (FormulaParams params : formulaParamsList) {
            BigDecimal score = weightedScores.get(params.getUserId())
                    .multiply(new BigDecimal("100"), MC)
                    .setScale(SCALE, RoundingMode.HALF_UP);

            FormulaParams resultParams = new FormulaParams();
//            SysUser sysUser = sysUserService.getById(params.getUserId()) ;
//            resultParams.setName(sysUser.getRealName());
            resultParams.setUserId(params.getUserId());
            resultParams.setEfficiency(params.getEfficiency());
            resultParams.setComplexity(params.getComplexity());
            resultParams.setBugRate(params.getBugRate());
            resultParams.setScore(score.toPlainString());

            result.add(resultParams);
        }
        List<FormulaParams> sortedList = result.stream()
                .sorted(Comparator.comparing(
                        o -> new BigDecimal(o.getScore()),
                        Comparator.reverseOrder()  // 倒序排列
                ))
                .collect(Collectors.toList());
        return sortedList;
    }


    public List<FormulaParams> evaluatePersonalAbility_manage(List<FormulaParams> formulaParamsList){
        if (formulaParamsList == null || formulaParamsList.isEmpty()){
            return Collections.emptyList();
        }

        // 1. 转换数据类型并收集最大值
        Map<String, BigDecimal> maxValues = new HashMap<>();
        Map<String, Map<String, BigDecimal>> userValues = new HashMap<>();

        // 首次遍历：转换数据类型并找到各指标最大值
        for (FormulaParams params : formulaParamsList) {
            try {
                BigDecimal efficiency = new BigDecimal(params.getEfficiency());
                BigDecimal complexity = new BigDecimal(params.getComplexity());


                // 更新各指标最大值
                maxValues.put("efficiency",  max(maxValues.get("efficiency"),  efficiency));
                maxValues.put("complexity",  max(maxValues.get("complexity"),  complexity));

                // 存储原始值（使用Java 8兼容的方式创建Map）
                Map<String, BigDecimal> valueMap = new HashMap<>();
                valueMap.put("efficiency",  efficiency);
                valueMap.put("complexity",  complexity);
                userValues.put(params.getUserId(),  valueMap);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format for user: " + params.getUserId());
            }
        }

        // 2. 计算加权总分
        Map<String, BigDecimal> weightedScores = new HashMap<>();
        Map<String, BigDecimal> weights = new HashMap<>();
        formulaConfig.getManageWeights().forEach((k,  v) -> weights.put(k,  new BigDecimal(v.toString())));

        // 计算每个用户的加权总分
        for (Map.Entry<String, Map<String, BigDecimal>> entry : userValues.entrySet())  {
            String userId = entry.getKey();
            Map<String, BigDecimal> values = entry.getValue();

            BigDecimal normalizedEfficiency = safeDivide(values.get("efficiency"),  maxValues.get("efficiency"));
            BigDecimal normalizedComplexity = safeDivide(values.get("complexity"),  maxValues.get("complexity"));

            BigDecimal weightedScore = normalizedEfficiency.multiply(weights.get("efficiency"),  MC)
                    .add(normalizedComplexity.multiply(weights.get("difficulty"),  MC), MC);

            weightedScores.put(userId,  weightedScore);
        }


        // 4. 生成最终结果
        List<FormulaParams> result = new ArrayList<>();
        for (FormulaParams params : formulaParamsList) {
            BigDecimal score = weightedScores.get(params.getUserId())
                    .multiply(new BigDecimal("100"), MC)
                    .setScale(SCALE, RoundingMode.HALF_UP);

            FormulaParams resultParams = new FormulaParams();
//            SysUser sysUser = sysUserService.getById(params.getUserId()) ;
//            resultParams.setName(sysUser.getRealName());
            resultParams.setUserId(params.getUserId());
            resultParams.setEfficiency(params.getEfficiency());
            resultParams.setComplexity(params.getComplexity());
            resultParams.setBugRate(params.getBugRate());
            resultParams.setScore(score.toPlainString());

            result.add(resultParams);
        }
        List<FormulaParams> sortedList = result.stream()
                .sorted(Comparator.comparing(
                        o -> new BigDecimal(o.getScore()),
                        Comparator.reverseOrder()  // 倒序排列
                ))
                .collect(Collectors.toList());
        return sortedList;
    }

    private BigDecimal max(BigDecimal a, BigDecimal b) {
        if (a == null) return b;
        if (b == null) return a;
        return a.compareTo(b)  > 0 ? a : b;
    }

    private BigDecimal safeDivide(BigDecimal numerator, BigDecimal denominator) {
        if (numerator == null || denominator == null || denominator.compareTo(BigDecimal.ZERO)  == 0) {
            return BigDecimal.ZERO;
        }
        return numerator.divide(denominator,  MC);
    }
}
