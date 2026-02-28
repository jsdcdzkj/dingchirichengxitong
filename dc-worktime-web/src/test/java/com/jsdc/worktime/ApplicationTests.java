package com.jsdc.worktime;

import com.jsdc.worktime.config.formula.FormulaConfig;
import com.jsdc.worktime.config.formula.FormulaParams;
import com.jsdc.worktime.config.formula.FormulaUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
class ApplicationTests {
    @Autowired
    private FormulaConfig formulaConfig;
    @Autowired
    private FormulaUtils formulaUtils;
    @Test
    void contextLoads() {
//        formulaConfig.getDifficulty().get("easy") ;
//
//        System.out.println(formulaConfig.getDifficulty().get("easy"));
        formulaConfig.getDifficulty().forEach((k,v)->{
            System.out.println(k+"  "+v);
        });

        formulaConfig.getWeights().forEach((k,v)->{
            System.out.println(k+"  "+v);
        });
        System.out.println(formulaConfig.getEfficiency());

    }

    @Test
    void contextLoads2() {
        List<FormulaParams> paramsList = Arrays.asList(
                new FormulaParams("user1", "80.12", "90.34", "70.56", "95.78"),
                new FormulaParams("user2", "90.00", "80.50", "85.25", "97.99"),
                new FormulaParams("user3", "70.75", "95.12", "75.33", "90.11")
        );

// 计算结果
        List<FormulaParams> result = formulaUtils.evaluateContribution(paramsList);

// 输出结果
        result.forEach(p  -> System.out.println(
                p.getUserId()  + ": " + p.getScore()  + "分"
        ));
    }

}
