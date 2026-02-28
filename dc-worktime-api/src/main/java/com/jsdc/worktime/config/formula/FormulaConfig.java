package com.jsdc.worktime.config.formula;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "formula")
@Data
@Component
public class FormulaConfig {
    //难度
    private Map<String, String> difficulty;

    //难度
    private Map<String, String> difficulty_f;

    //难度
    private Map<String, String> difficulty_h;
    //项目贡献度权重
    private Map<String, String> weights;
    //开发人员个人能力值权重
    private Map<String, String> devWeights;
    //产品经理、项目经理权重
    private Map<String, String> manageWeights;
    //bug严重程度
    private Map<String, String> severity;
    //效率限制
    private String efficiency;
    //bug率倒数，超过这个代表满分
    private String bugFreeRate;
}
