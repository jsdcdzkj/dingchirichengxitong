package com.jsdc.worktime.config;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PageQuery<T> {
    private Integer page = 1;  // 默认第1页
    private Integer size = 10; // 默认每页10条
    private List<String> sorts = new ArrayList<>(); // ["createTime,desc", "id,asc"]
    private T params;

}