package com.jsdc.portal;

import com.jsdc.worktime.config.formula.FormulaConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTests {
    @Autowired
    private FormulaConfig formulaConfig;
    @Test
    void contextLoads() {
        formulaConfig.getDifficulty().forEach((k,v)->{
            System.out.println(k+"  "+v);
        });
        formulaConfig.getWeights().forEach((k,v)->{
            System.out.println(k+"  "+v);
        });
        System.out.println(formulaConfig.getEfficiency());

    }

}
