package com.jsdc.worktime.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanTreeVo {

    private Integer id;
    /**
     * 类型 1. 迭代 2. 阶段 3. 任务
     */
    private Integer type;
    /**
     * 标题
     */
    private String title;
    /**
     * 父id
     */
    private Integer pid;

    private List<PlanTreeVo> children;

    public PlanTreeVo initChildren() {
        if (this.children  == null) {
            this.children  = new ArrayList<>(8); // 预分配空间提升性能
        }
        return this;
    }
}
