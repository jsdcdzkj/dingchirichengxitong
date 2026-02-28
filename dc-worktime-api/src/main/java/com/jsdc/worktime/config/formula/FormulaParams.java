package com.jsdc.worktime.config.formula;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class FormulaParams {
    //用户ID
    private String userId;
    //工作量
    private String workload;
    //效率
    private String efficiency;
    //复杂度
    private String complexity;
    //bug率
    private String bugRate;
    //分值
    private String score;
    //组名
    private String groupName;
    //姓名
    private String name;
    private Integer count;

    //bug数
    private Integer bugCount;
    // 功能点*难度
    private String complexityNum;

    public FormulaParams(String userId, String workload, String efficiency, String complexity, String bugRate) {
        this.userId = userId;
        this.workload = workload;
        this.efficiency = efficiency;
        this.complexity = complexity;
        this.bugRate = bugRate;
    }

    public FormulaParams() {
    }
}
