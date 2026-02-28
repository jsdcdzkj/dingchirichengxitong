package com.jsdc.worktime.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanProcessVo {

    private String id;

    // 标题
    private String title;

    // type
    private String type;

    // 开始日期 2025-03-18
    private String startDate;

    // 结束日期
    private String endDate;

    // 进度
    private BigDecimal progress;

    private String parentId;

    private Date createTime;

    private List<PlanProcessVo> children;

}
